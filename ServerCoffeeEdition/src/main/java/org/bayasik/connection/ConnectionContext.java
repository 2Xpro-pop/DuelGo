package org.bayasik.connection;

import org.bayasik.commands.CloseConnectionLocker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public interface ConnectionContext  {
    void close();
    <T> T get(Class<T> type);
    <T> void put(Class<? extends T> type, T instance);

    public static ConnectionContext fromSocket(Socket socket) throws IOException {
        var context = new ConnectionContextImpl();

        context.put(Socket.class, socket);
        context.put(InputStream.class, socket.getInputStream());
        context.put(OutputStream.class, socket.getOutputStream());
        context.put(CloseConnectionLocker.class, context);

        return context;
    }

    class ConnectionContextImpl implements ConnectionContext, CloseConnectionLocker {
        HashMap<Class<?>, Object> dictionary = new HashMap<>();
        @Override
        public synchronized void close() {
            notifyAll();
        }

        @Override
        public synchronized void waitClose() {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T> T get(Class<T> type) {
            return (T) dictionary.get(type);
        }

        @Override
        public <T> void put(Class<? extends T> type, T instance) {
            dictionary.put(type, instance);
        }


    }
}
