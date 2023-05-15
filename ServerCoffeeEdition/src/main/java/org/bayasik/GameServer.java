package org.bayasik;

import com.google.inject.Injector;
import org.bayasik.connection.CloseConnectionLocker;
import org.bayasik.connection.ConnectionContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class GameServer {
    private final int port;
    private final Injector injector;
    private final SessionMiddlewaresHandler sessionMiddlewaresHandler;

    public GameServer(int port, Injector injector, SessionMiddlewaresHandler sessionMiddlewaresHandler) {
        this.port = port;
        this.injector = injector;
        this.sessionMiddlewaresHandler = sessionMiddlewaresHandler;
    }

    public void start(){
        try {
            var socketServer = new ServerSocket(port);
            while (true) {
                var socket = socketServer.accept();
                var connectionContext = ConnectionContext.fromSocket(socket);

                connectionContext.put(Injector.class, injector);
                connectionContext.put(GameServer.class, this);

                var socketThread = new SocketThread(this, connectionContext);
                socketThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't start server on port " + port, e);
        }
    }

    private static class SocketThread extends Thread
    {
        private final GameServer gameServer;
        private final ConnectionContext connectionContext;

        private SocketThread(GameServer gameServer, ConnectionContext connectionContext) {
            this.gameServer = gameServer;
            this.connectionContext = connectionContext;
        }

        @Override
        public synchronized void run() {
            var socket = connectionContext.get(Socket.class);
            var connectionCloseLocker = connectionContext.get(CloseConnectionLocker.class);

            gameServer.sessionMiddlewaresHandler.handleOpenConnection(connectionContext);

            while (socket.isConnected() && !socket.isClosed()){
            }

            gameServer.sessionMiddlewaresHandler.handleCloseConnection(connectionContext);
        }
    }
}
