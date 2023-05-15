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
        IChainOfResponsibility[] handlers = new IChainOfResponsibility[descriptors.length];

        for (int i = 0; i < descriptors.length; i++) {
            handlers[i] = descriptors[i].getInstance();
        }

        var invoker = new IChainOfResponsibility.NextInvoker(handlers);

        invoker.accept(context, null);
    }

}
