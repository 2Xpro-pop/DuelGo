package org.bayasik.middleware;

import org.bayasik.connection.ConnectionContext;

public class ConnectionLiverChecker extends Thread {
    private double lastTime = System.currentTimeMillis();
    private final ConnectionContext context;

    public ConnectionLiverChecker(ConnectionContext context) {
        this.context = context;
    }

    @Override
    public void run(){
        while (true){
            try {
                Thread.sleep(10_000);
                if (System.currentTimeMillis() - lastTime > 5000){
                    context.close();
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public double getLastTime(){
        return lastTime;
    }

    public void updateLastTime(){
        lastTime = System.currentTimeMillis();
    }

}
