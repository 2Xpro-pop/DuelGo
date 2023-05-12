package org.bayasik.middleware;

import org.bayasik.connection.ConnectionContext;
import org.bayasik.connection.IChainOfConnectionHandler;

import java.net.Socket;

public class ConnectionLiverCheckerMiddleware implements IChainOfConnectionHandler {

    @Override
    public void accept(ConnectionContext context, IChainOfConnectionHandler next) {
        var connectionLiverChecker = new ConnectionLiverChecker(context);
        connectionLiverChecker.start();

        context.put(connectionLiverChecker);

        next.accept(context, next);
    }
}
