package org.bayasik;

import org.bayasik.connection.ConnectionContext;

public interface SessionMiddlewaresHandler {
    void handleOpenConnection(ConnectionContext context);
    void handleCloseConnection(ConnectionContext context);
}
