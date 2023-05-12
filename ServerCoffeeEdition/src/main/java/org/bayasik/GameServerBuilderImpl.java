package org.bayasik;

import org.bayasik.commands.*;
import org.bayasik.connection.ChainOfResponsibilityDescriptor;
import org.bayasik.connection.ConnectionContext;
import org.bayasik.connection.IChainOfConnectionHandler;
import org.bayasik.connection.InjectableChainOfResponsibility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServerBuilderImpl extends GameServerBuilder {
    private final Map<Short, String> commands = new HashMap<>();
    private final List<ChainOfResponsibilityDescriptor> chainsOfOpenConnection = new ArrayList<>();
    private final List<ChainOfResponsibilityDescriptor> chainsOfCloseConnection = new ArrayList<>();
    private final List<CommandHandlerDescriptor> commandHandlers = new ArrayList<>();
    private final List<CommandDescriptor> anonymousCommands = new ArrayList<>();

    @Override
    public void readCommands(Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) &&
                    Modifier.isFinal(field.getModifiers()) &&
                    Modifier.isPublic(field.getModifiers()) &&
                    field.getType() == short.class) {
                try {
                    short value = field.getShort(null);
                    String name = field.getName();
                    commands.put(value, name);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void useOpen(Class<? extends IChainOfConnectionHandler> type) {
        chainsOfOpenConnection.add(new ChainOfResponsibilityDescriptor(type));
    }

    @Override
    public void useOpen(IChainOfConnectionHandler accept) {
        chainsOfOpenConnection.add(new ChainOfResponsibilityDescriptor(accept));
    }

    @Override
    public void useClose(Class<? extends IChainOfConnectionHandler> type) {
        chainsOfCloseConnection.add(new ChainOfResponsibilityDescriptor(type));
    }

    @Override
    public void useClose(IChainOfConnectionHandler close) {
        chainsOfCloseConnection.add(new ChainOfResponsibilityDescriptor(close));
    }

    @Override
    public void addCommandHandler(Class<? extends CommandHandler> type) {
        var methods = getEndpoints(type);
        commandHandlers.add(new CommandHandlerDescriptor(type, methods));
    }

    @Override
    public void addCommandHandler(CommandHandler handler) {
        var methods = getEndpoints(handler.getClass());
        commandHandlers.add(new CommandHandlerDescriptor(handler, methods));
    }

    @Override
    public void addCommandHandler(short command, AnonymousCommand anonymousCommand){
        anonymousCommands.add(new CommandDescriptor(command, anonymousCommand));
    }

    @Override
    public GameServer build() {
        var middlewares = createMiddlewares();

        return new GameServer(port, injector, middlewares);
    }

    private SessionMiddlewaresHandler createMiddlewares(){
        for(var chainOfOpenConnection : chainsOfOpenConnection){
            chainOfOpenConnection.setInjector(injector);
        }
        var middlewareOfOpenConnection = new InjectableChainOfResponsibility(
                chainsOfOpenConnection.toArray(new ChainOfResponsibilityDescriptor[0])
        );

        for(var chainOfCloseConnection : chainsOfCloseConnection){
            chainOfCloseConnection.setInjector(injector);
        }
        var middlewareOfCloseConnection = new InjectableChainOfResponsibility(
                chainsOfCloseConnection.toArray(new ChainOfResponsibilityDescriptor[0])
        );

        return new SessionMiddlewaresHandler()
        {
            @Override
            public void handleCloseConnection(ConnectionContext context) {
                middlewareOfCloseConnection.accept(context);
            }

            @Override
            public void handleOpenConnection(ConnectionContext context) {
                middlewareOfOpenConnection.accept(context);
            }
        };
    }

    private Iterable<CommandDescriptor> getEndpoints(Class<? extends CommandHandler> type) {
        var methods = new ArrayList<CommandDescriptor>();
        for (Method method : type.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) &&
                    Modifier.isFinal(method.getModifiers()) &&
                    Modifier.isStatic(method.getModifiers()) &&
                    method.getReturnType() == void.class &&
                    method.isAnnotationPresent(Command.class)) {
                var annotation = method.getAnnotation(Command.class);
                short commandId = annotation.value();
                methods.add(new CommandDescriptor(method, commandId));
            }
        }
        return methods;
    }
}
