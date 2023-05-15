package org.bayasik.commands;

import java.lang.reflect.Method;

public class CommandDescriptor {
    public final Method method;
    public final short commandId;
    public final AnonymousCommand anonymousCommand;

    private CommandHandler instance;

    public CommandDescriptor(Method method, short commandId) {
        this.method = method;
        this.commandId = commandId;
        this.anonymousCommand = null;
        ;
    }

    public CommandDescriptor(short commandId, AnonymousCommand anonymousCommand) {
        this.commandId = commandId;
        this.anonymousCommand = anonymousCommand;
        this.method = null;
    }

    public CommandHandler getInstance() {
        return instance;
    }

    public void setInstance(CommandHandler instance) {
        this.instance = instance;
    }

    public boolean isAnonymous() {
        return anonymousCommand != null;
    }

}
