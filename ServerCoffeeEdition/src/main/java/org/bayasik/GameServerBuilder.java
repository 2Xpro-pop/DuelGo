package org.bayasik;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bayasik.commands.*;
import org.bayasik.connection.IChainOfResponsibility;
import org.bayasik.messages.BinaryMessageReaderStrategy;
import org.bayasik.messages.MessageReaderStrategy;

public abstract class GameServerBuilder {
    protected Injector injector;
    protected int port;
    public abstract void readCommands(Class<?> type);

    public abstract void useOpen(Class<? extends IChainOfResponsibility> type);
    public abstract void useOpen(IChainOfResponsibility accept);

    public abstract void useClose(Class<? extends IChainOfResponsibility> type);

    public abstract void useClose(IChainOfResponsibility close);

    public abstract void addCommandHandler(Class<? extends CommandHandler> type);

    public abstract void addCommandHandler(CommandHandler handler);

    public abstract void addCommandHandler(short command,AnonymousCommand anonymousCommand);

    public abstract void configureInjector(com.google.inject.Module module);

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

        var injector = Guice.createInjector((binder -> {
            binder.bind(MessageReaderStrategy.class).to(BinaryMessageReaderStrategy.class);
        }));

        builder.setInjector(injector);

        return builder;
    }

}
