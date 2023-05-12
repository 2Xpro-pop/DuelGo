package org.bayasik;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bayasik.commands.*;
import org.bayasik.connection.IChainOfConnectionHandler;

public abstract class GameServerBuilder {
    protected Injector injector;
    protected int port;
    public abstract void readCommands(Class<?> type);

    public abstract void useOpen(Class<? extends IChainOfConnectionHandler> type);
    public abstract void useOpen(IChainOfConnectionHandler accept);

    public abstract void useClose(Class<? extends IChainOfConnectionHandler> type);

    public abstract void useClose(IChainOfConnectionHandler close);

    public abstract void addCommandHandler(Class<? extends CommandHandler> type);

    public abstract void addCommandHandler(CommandHandler handler);

    public abstract void addCommandHandler(short command,AnonymousCommand anonymousCommand);

    public abstract GameServer build();

    public Injector getInjector() {
        return injector;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static GameServerBuilder create() {
        var builder =  new GameServerBuilderImpl();

        var injector = Guice.createInjector();
        builder.setInjector(injector);


        return builder;
    }

}
