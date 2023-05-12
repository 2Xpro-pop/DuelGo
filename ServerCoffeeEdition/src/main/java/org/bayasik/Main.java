package org.bayasik;

import com.google.inject.Guice;

public class Main {
    public static void main(String[] args) {
        var injector = Guice.createInjector();
        injector = injector.createChildInjector(binder -> {

        });

        var builder = GameServerBuilder.create();

        builder.readCommands(Commands.class);

        builder.useOpen((context, next) -> {
            System.out.println("Hello world!");
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