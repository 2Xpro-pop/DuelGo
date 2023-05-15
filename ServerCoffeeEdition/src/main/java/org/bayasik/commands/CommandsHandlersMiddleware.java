package org.bayasik.commands;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.bayasik.connection.ChainOfResponsibilityDescriptor;
import org.bayasik.connection.ConnectionContext;
import org.bayasik.connection.IChainOfConnectionHandler;
import org.bayasik.messages.MessageContext;
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

        messageThread.chainOfGettingMessage.add(new ChainOfResponsibilityDescriptor(
                CommandsHandlersPerMessageMiddleware.class)
        );

    }

    static class CommandsHandlersPerMessageMiddleware implements IChainOfConnectionHandler {
        private final CommandDescriptor[] descriptors;
        private final MessageContext messageContext;
        private final Injector injector;

        @Inject
        public CommandsHandlersPerMessageMiddleware(CommandDescriptor[] descriptors, MessageContext messageContext, Injector injector) {
            this.descriptors = descriptors;
            this.messageContext = messageContext;
            this.injector = injector;
        }

        @Override
        public void accept(ConnectionContext context, IChainOfConnectionHandler next) {
            var command = new CommandsExplorer(descriptors, context);
            var cmd = command.readCommand(messageContext.lastMessage());

            activateCommandHandler(cmd);
            command.InvokeCommand(cmd);

            next.accept(context, next);

        }

        private void activateCommandHandler(CommandToken cmd) {
            for(var descriptor : descriptors) {
                if(descriptor.isAnonymous()) continue;

                descriptor.setInstance(null);
            }
            for(var descriptor : descriptors) {
                if(descriptor.isAnonymous() || descriptor.commandId != cmd.commandId()) continue;

                if(descriptor.getInstance() == null){
                    var instance = injector.getInstance(descriptor.method.getDeclaringClass());
                    descriptor.setInstance((CommandHandler) instance);
                }

                break;

            }
        }
    }
}
