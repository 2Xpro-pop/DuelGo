package org.bayasik;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
    public static void main(String[] args) {
        var injector = Guice.createInjector();

        var builder = new GameServerBuilder();

        builder.readCommands(Commands.class);

        builder.use((context, next) -> {
            System.out.println("Hello world!");
            next.accept(context, next);
        });

        builder.addCommandHandler(Commands.CREATE_ROOM, (context) -> {
            var server = context.get(GameServer.class);
            var room = server.createRoom();
            context.close();
        });

    }
}