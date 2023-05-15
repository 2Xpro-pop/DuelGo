package org.bayasik.messages;

import com.google.inject.Injector;
import org.bayasik.connection.ConnectionContext;
import org.bayasik.connection.IChainOfResponsibility;

public class MessageMiddlewareHandler implements IChainOfResponsibility {
    @Override
    public void accept(ConnectionContext context, IChainOfResponsibility next) {
        var messageReaderStrategy = context.get(Injector.class).getInstance(MessageReaderStrategy.class);
        var messageThread = new MessageThread(context, messageReaderStrategy);

        context.put(MessageThread.class, messageThread);

        next.accept(context, next);

        messageThread.start();
    }
}

