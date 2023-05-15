package org.bayasik;

import org.bayasik.commands.*;
import org.bayasik.connection.ChainOfResponsibilityDescriptor;
import org.bayasik.connection.ConnectionContext;
import org.bayasik.connection.IChainOfConnectionHandler;
import org.bayasik.connection.InjectableChainOfResponsibility;
import org.bayasik.messages.BinaryMessageReaderStrategy;
import org.bayasik.messages.MessageReaderStrategy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class GameServerBuilderImpl extends GameServerBuilder {
    private final Map<Short, String> commands = new HashMap<>();
    private final List<ChainOfResponsibilityDescriptor> chainsOfOpenConnection = new ArrayList<>();
    private final List<ChainOfResponsibilityDescriptor> chainsOfCloseConnection = new ArrayList<>();
    private final List<CommandHandlerDescriptor> commandHandlers = new ArrayList<>();
    private final List<CommandDescriptor> commandDescriptors = new ArrayList<>();

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
        for (var method : methods) {
            method.setInstance(handler);
            commandDescriptors.add(method);
        }
    }

    @Override
    public void addCommandHandler(short command, AnonymousCommand anonymousCommand){
        commandDescriptors.add(new CommandDescriptor(command, anonymousCommand));
    }

    @Override
    public void configureInjector(com.google.inject.Module module){
        var injector = getInjector().createChildInjector(module);
        setInjector(injector);
    }

    @Override
    public GameServer build() {
        configureInjector((binder -> {
            binder.bind(CommandDescriptor[].class).toInstance(commandDescriptors.toArray(new CommandDescriptor[0]));
            binder.bind(Map.class).toInstance(Collections.unmodifiableMap(commands));
        }));

        var middlewares = createMiddlewares();

        return new GameServer(port, injector, middlewares);
    }

    private SessionMiddlewaresHandler createMiddlewares(){

        var middlewareOfOpenConnection = new InjectableChainOfResponsibility(
            chainsOfOpenConnection.toArray(new ChainOfResponsibilityDescriptor[0])
        );
        middlewareOfOpenConnection.setInjector(injector);

        var middlewareOfCloseConnection = new InjectableChainOfResponsibility(
            chainsOfCloseConnection.toArray(new ChainOfResponsibilityDescriptor[0])
        );
        middlewareOfCloseConnection.setInjector(injector);

        return new SessionMiddlewaresHandler()
        {
            @Override
            public void handleOpenConnection(ConnectionContext context) {
                middlewareOfOpenConnection.accept(context);
            }

            @Override
            public void handleCloseConnection(ConnectionContext context) {
                middlewareOfCloseConnection.accept(context);
            }
        };
    }

    private Collection<CommandDescriptor> getEndpoints(Class<? extends CommandHandler> type) {
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
