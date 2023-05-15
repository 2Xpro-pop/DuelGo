package org.bayasik;

import com.google.inject.Guice;
import org.bayasik.middleware.ConnectionLiverCheckerMiddleware;
import org.bayasik.messages.MessageMiddlewareHandler;

public class Main {
    public static void main(String[] args) {

        var builder = GameServerBuilder.create();

        builder.readCommands(Commands.class);

        builder.setPort(14908);

        builder.useOpen(ConnectionLiverCheckerMiddleware.class);
        builder.useOpen(MessageMiddlewareHandler.class);

        builder.useOpen((context, next) -> {
            System.out.println("Hello world!");
            next.accept(context, next);
        });

        builder.useClose((context, next) -> {
            System.out.println("Goodbye world!");
            next.accept(context, next);
        });


        builder.addCommandHandler(Commands.CREATE_ROOM, (context) -> {
            var server = context.get(GameServer.class);
            var room = server.createRoom();
            context.close();
        });

        var server = builder.build();

        server.start();
    }
}