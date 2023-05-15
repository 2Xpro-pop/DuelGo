package org.bayasik.middleware;

import org.bayasik.connection.ConnectionContext;
import org.bayasik.connection.IChainOfResponsibility;

public class ConnectionLiverCheckerMiddleware implements IChainOfResponsibility {

    @Override
    public void accept(ConnectionContext context, IChainOfResponsibility next) {
        var connectionLiverChecker = new ConnectionLiverChecker(context);
        connectionLiverChecker.start();

        context.put(connectionLiverChecker);

        next.accept(context, next);
    }
}
