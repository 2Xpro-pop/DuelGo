package org.bayasik.middleware;

import com.google.inject.Inject;
import org.bayasik.commands.CommandDescriptor;
import org.bayasik.commands.CommandsExplorer;
import org.bayasik.connection.ChainOfResponsibilityDescriptor;
import org.bayasik.connection.ConnectionContext;
import org.bayasik.connection.IChainOfConnectionHandler;
import org.bayasik.messages.MessageThread;

public class CommandsHandlersMiddleware implements IChainOfConnectionHandler {
    private final CommandDescriptor[] descriptors;

    @Inject
    public CommandsHandlersMiddleware(CommandDescriptor[] descriptors) {
        this.descriptors = descriptors;
    }

    @Override
    public void accept(ConnectionContext context, IChainOfConnectionHandler next) {

        next.accept(context, next);

        var messageThread = context.get(MessageThread.class);
        var commandsExplorer = new CommandsExplorer(descriptors);

        messageThread.chainOfGettingMessage.add(new ChainOfResponsibilityDescriptor(
            (ctx, handler) -> {

                handler.accept(ctx, handler);
            }
        ));


    }
}
