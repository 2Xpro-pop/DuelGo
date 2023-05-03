package org.bayasik.commands;

import org.bayasik.connection.ConnectionContext;

public class AnonymousCommandHandler implements CommandHandler {
    private final short commandId;
    private final AnonymousCommand command;
    private ConnectionContext context;

    public AnonymousCommandHandler(short commandId, AnonymousCommand command) {
        this.commandId = commandId;
        this.command = command;
    }

    @Override
    public void setContext(ConnectionContext context) {
        this.context = context;
    }

    @Override
    public boolean canHandle() {
        return true;
    }

    public void handle() {
        command.handle(context);
    }
}
