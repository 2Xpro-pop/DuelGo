package org.bayasik.connection;

import com.google.inject.Injector;

public class ChainOfResponsibilityDescriptor
{
    private final Class<? extends IChainOfResponsibility> type;
    private final IChainOfResponsibility instance;
    private Injector injector;

    public ChainOfResponsibilityDescriptor(Class<? extends IChainOfResponsibility> type) {
        this.type = type;
        this.instance = null;
    }

    public ChainOfResponsibilityDescriptor(IChainOfResponsibility instance) {
        this.type = null;
        this.instance = instance;
    }

    public IChainOfResponsibility getInstance() {
        if (instance == null) {
            return injector.getInstance(type);
        }
        return instance;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}