package org.bayasik.connection;

import com.google.inject.Injector;

public class InjectableChainOfResponsibility {
    private final ChainOfResponsibilityDescriptor[] descriptors;

    public InjectableChainOfResponsibility(ChainOfResponsibilityDescriptor[] descriptors) {
        this.descriptors = descriptors;
    }

    public void setInjector(Injector injector) {
        for (var descriptor : descriptors) {
            descriptor.setInjector(injector);
        }
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
