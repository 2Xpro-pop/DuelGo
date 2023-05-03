package org.bayasik.connection;

import com.google.inject.Injector;

public class ChainOfResponsibilityDescriptor
{
    private final Class<? extends IChainOfConnectionAccept> type;
    private final IChainOfConnectionAccept instance;
    private Injector injector;

    public ChainOfResponsibilityDescriptor(Class<? extends IChainOfConnectionAccept> type) {
        this.type = type;
        this.instance = null;
    }

    public ChainOfResponsibilityDescriptor(IChainOfConnectionAccept instance) {
        this.type = null;
        this.instance = instance;
    }

    public IChainOfConnectionAccept getInstance() {
        if (instance == null) {
            return injector.getInstance(type);
        }
        return instance;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}