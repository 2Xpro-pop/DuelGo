package org.bayasik.connection;

public class InjectableChainOfResponsibility {
    private final ChainOfResponsibilityDescriptor[] descriptors;

    public InjectableChainOfResponsibility(ChainOfResponsibilityDescriptor[] descriptors) {
        this.descriptors = descriptors;
    }

    public void accept(ConnectionContext context) {
        IChainOfConnectionHandler[] handlers = new IChainOfConnectionHandler[descriptors.length];

        for (int i = 0; i < descriptors.length; i++) {
            handlers[i] = descriptors[i].getInstance();
        }

        var invoker = new IChainOfConnectionHandler.NextInvoker(handlers);

        invoker.accept(context, null);
    }

}
