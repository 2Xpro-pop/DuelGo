package org.bayasik;

import org.bayasik.commands.Command;
import org.bayasik.commands.CommandHandler;
import org.bayasik.connection.ConnectionContext;

public class TestCommandHandler implements CommandHandler {
    private ConnectionContext context;

    @Override
    public void setContext(ConnectionContext context) {
        this.context = context;
    }

    @Override
    public boolean canHandle() {
        return true;
    }

    @Command(Commands.JOIN_ROOM)
    public void argTest(int a, short b, String c) {
        System.out.println("ArgTest: " + a + " " + b + " " + c);
    }
}
