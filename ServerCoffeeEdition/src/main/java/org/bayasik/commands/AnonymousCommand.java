package org.bayasik.commands;

import org.bayasik.connection.ConnectionContext;

@FunctionalInterface
public interface AnonymousCommand {
    void handle(ConnectionContext context);
}