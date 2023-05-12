package org.bayasik;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bayasik.middleware.ConnectionLiverChecker;
import org.bayasik.middleware.ConnectionLiverCheckerMiddleware;

public class Main {
    public static void main(String[] args) {
        var injector = Guice.createInjector();
        injector = injector.createChildInjector(binder -> {

        });

        var builder = GameServerBuilder.create();

        builder.readCommands(Commands.class);

        builder.setPort(14908);

        builder.useOpen(ConnectionLiverCheckerMiddleware.class);

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