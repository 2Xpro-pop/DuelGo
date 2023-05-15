package org.bayasik.commands;

import org.bayasik.connection.ConnectionContext;

public interface CommandHandler {
    void setContext(ConnectionContext context);
    default boolean canHandle() {
        return true;
    }
}
