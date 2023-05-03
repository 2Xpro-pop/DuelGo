package org.bayasik;

import com.google.inject.Injector;
import org.bayasik.commands.*;
import org.bayasik.connection.ChainOfResponsibilityDescriptor;
import org.bayasik.connection.IChainOfConnectionAccept;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServerBuilder {
    private Map<Short, String> commands = new HashMap<>();
    private List<ChainOfResponsibilityDescriptor> chain = new ArrayList<>();
    private List<CommandHandlerDescriptor> commandHandlers = new ArrayList<>();
    private List<CommandDescriptor> anonymousCommands = new ArrayList<>();
    private Injector injector;
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

    public void use(Class<? extends IChainOfConnectionAccept> type) {
        chain.add(new ChainOfResponsibilityDescriptor(type));
    }
    public void use(IChainOfConnectionAccept accept) {
        chain.add(new ChainOfResponsibilityDescriptor(accept));
    }

    public void addCommandHandler(Class<? extends CommandHandler> type) {
        var methods = getEndpoints(type);
        commandHandlers.add(new CommandHandlerDescriptor(type, methods));
    }

    public void addCommandHandler(CommandHandler handler) {
        var methods = getEndpoints(handler.getClass());
        commandHandlers.add(new CommandHandlerDescriptor(handler, methods));
    }

    public void addCommandHandler(short command,AnonymousCommand anonymousCommand){
        anonymousCommands.add(new CommandDescriptor(command, anonymousCommand));
    }

    public Injector getInjector() {
        return injector;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
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
