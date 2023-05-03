package org.bayasik.commands;

import com.google.inject.Injector;


public class CommandHandlerDescriptor {
    private final Class<? extends CommandHandler> type;
    private final CommandHandler instance;
    private final Iterable<CommandDescriptor> commands;
    private Injector injector;

    public CommandHandlerDescriptor(Class<? extends CommandHandler> type, Iterable<CommandDescriptor> commands) {
        this.type = type;
        this.commands = commands;
        this.instance = null;
    }

    public CommandHandlerDescriptor(CommandHandler instance, Iterable<CommandDescriptor> commands) {
        this.type = null;
        this.instance = instance;
        this.commands = commands;
    }


    public CommandHandler getInstance() {
        if (instance == null) {
            return injector.getInstance(type);
        }
        return instance;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}
