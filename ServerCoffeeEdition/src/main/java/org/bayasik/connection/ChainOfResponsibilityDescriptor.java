package org.bayasik.connection;

import com.google.inject.Injector;
import org.bayasik.connection.IChainOfConnectionHandler;

public class ChainOfResponsibilityDescriptor
{
    private final Class<? extends IChainOfConnectionHandler> type;
    private final IChainOfConnectionHandler instance;
    private Injector injector;

    public ChainOfResponsibilityDescriptor(Class<? extends IChainOfConnectionHandler> type) {
        this.type = type;
        this.instance = null;
    }

    public ChainOfResponsibilityDescriptor(IChainOfConnectionHandler instance) {
        this.type = null;
        this.instance = instance;
    }

    public IChainOfConnectionHandler getInstance() {
        if (instance == null) {
            return injector.getInstance(type);
        }
        return instance;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}