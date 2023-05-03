package org.bayasik.connection;

public interface ConnectionContext {
    void close();
    <T> T get(Class<T> type);
    <T> void put(Class<T> type, T instance);
}
