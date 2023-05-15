package org.bayasik;

import com.google.inject.Guice;
import org.bayasik.commands.CommandsHandlersMiddleware;
import org.bayasik.middleware.ConnectionLiverCheckerMiddleware;
import org.bayasik.messages.MessageMiddlewareHandler;

public class TestServer {
    public static void main(String[] args) {

        var builder = GameServerBuilder.create();

        builder.readCommands(Commands.class);

        builder.setPort(14908);

        builder.useOpen(ConnectionLiverCheckerMiddleware.class);
        builder.useOpen(MessageMiddlewareHandler.class);
        builder.useOpen(CommandsHandlersMiddleware.class);

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
            System.out.println("Command work!");
            context.close();
        });

        builder.addCommandHandler(TestCommandHandler.class);

        var server = builder.build();

        server.start();
    }
}