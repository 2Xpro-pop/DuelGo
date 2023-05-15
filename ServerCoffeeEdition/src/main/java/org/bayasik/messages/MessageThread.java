package org.bayasik.messages;

import com.google.inject.Injector;
import com.google.inject.internal.SingletonScope;
import org.bayasik.PerMessage;
import org.bayasik.connection.ChainOfResponsibilityDescriptor;
import org.bayasik.connection.ConnectionContext;
import org.bayasik.connection.InjectableChainOfResponsibility;
import org.bayasik.middleware.ConnectionLiverChecker;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class MessageThread extends Thread {
    public final ArrayList<ChainOfResponsibilityDescriptor> chainOfGettingMessage = new ArrayList<>();
    private final ConnectionContext connectionContext;
    private final MessageReaderStrategy messageReaderStrategy;

    public MessageThread(ConnectionContext connectionContext, MessageReaderStrategy messageReaderStrategy) {
        this.connectionContext = connectionContext;
        this.messageReaderStrategy = messageReaderStrategy;
    }

    @Override
    public void run() {
        var socket = connectionContext.get(Socket.class);
        var middlewareOfGettingMessage = new InjectableChainOfResponsibility(
            chainOfGettingMessage.toArray(new ChainOfResponsibilityDescriptor[0])
        );
        var connectionLiverChecker = connectionContext.get(ConnectionLiverChecker.class);

        try {
            var inputStream = socket.getInputStream();

            while (!socket.isClosed()) {

                if(inputStream.available() == 0) {
                    continue;
                }

                connectionLiverChecker.updateLastTime();

                var injector = connectionContext.get(Injector.class).createChildInjector(binder -> {
                    binder.bindScope(PerMessage.class, new SingletonScope());
                });
                middlewareOfGettingMessage.setInjector(injector);

                var bytes = messageReaderStrategy.read(inputStream);
                var messageContext = injector.getInstance(MessageContext.class);
                messageContext.setLastMessage(bytes);

                middlewareOfGettingMessage.accept(connectionContext);

                System.out.println("Message: " + messageContext);

                connectionLiverChecker.updateLastTime();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
