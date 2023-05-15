# ServerCoffeeEdition
This is a library for creating game servers. My library helps create a server that uses TCP connections. It utilizes the chain of responsibility design pattern to open and close connections and provides a default implementation for a command handler (which is similar to controllers). It also uses Guice for dependency injection.

#### Preporotion 
To configure the builder to use the default middlewares.
```java
public class TestServer {
    public static void main(String[] args) {

        var builder = GameServerBuilder.create();

        builder.setPort(14908);

        builder.useOpen(ConnectionLiverCheckerMiddleware.class); // To prevent idle connections, it is recommended to automatically close the connection after 5 seconds of inactivity.
        builder.useOpen(MessageMiddlewareHandler.class); // It is important to use the correct tool for command handlers
        builder.useOpen(CommandsHandlersMiddleware.class); // Add support for command handlers, important use after MessageMiddlewareHandler

        var server = builder.build();

        server.start();
    }
}
```

### Tips

#### Connection context

Connection context is a class that holds all the information about the connection. It is used by the middlewares and command handlers. You can add your own information to the connection context. For example:
```java
connectionContext.put(MyCustomClass.class, new MyCustomClass());
```

Default connection context classes:
- Socket
- Injector

#### DI

Default dependecies is:
- MessageReaderStrategy - default implementation is BinaryMessageReaderStrategy
- CommandDescriptor[] - array of command descriptors
- Map<Short, String> - map of commands and their names(used for debug, need used builder.readCommands)

You can override the default dependencies by using the configureInjector method. For example:
```java
builder.configureInjector(binder -> {
    binder.bind(MessageReaderStrategy.class).to(MyMessageReaderStrategy.class);
});
```

Scope of dependencies:
- default
- Singleton - dependency will be created once
- PerMessage - dependency will be created for each message

#### builder.readCommands
Not necessary, but you can use builder.readCommands() to associate command names with their values. This method uses reflection to read the names of the commands from the command class, so that later you can refer to them by name rather than by number. For example, if the command class has a static field CREATE_ROOM with value 11, the readCommands() method will associate the name "CREATE_ROOM" with the value 11. This can be useful for logging or for reference purposes
```java
public class Commands {
    ...
    public static final short CREATE_ROOM = 11;
    ...
}

builder.readCommands(Commands.class);
```


#### MessageMiddlewareHandler

If you override the binding of MessageReaderStrategy in DI, the command handler will not work because it's used by MessageMiddlewareHandler. Although this violates OOP principles, if you need to create custom command handlers, you can do so by extending the existing CommandHandler and registering it with the server builder.

After creating a custom command handler, you need to add it to the builder. For example:
```java
builder.configureInjector(binder -> {
    binder.bind(MessageReaderStrategy.class).to(MyMessageReaderStrategy.class);
});
```

Then you need message handler event listener. For example:
```java
builder.useOpen(MessageMiddlewareHandler.class); // It is important to use the correct tool for command handlers
builder.useOpen((context, next) -> {
    var messageThread = context.get(MessageThread.class);

    messageThread.chainOfGettingMessage.add(new CommandDescriptor(MyCustomMessageHandler.class));

    next.accept(context, next);
});

class MyCustomMessageHandler implements IChainOfResponsibility {
    private final MessageContext messageContext; // dependency in "PerMessage" scope
    private final CommandDescriptor[] commandDescriptors; // all commands which registered in builder
    private final Map<Short, String> commands; // all commands which registered in builder by readCommands

    public MyCustomMessageHandler(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    @Override
    public void accept(ConnectionContext context, IChainOfResponsibility next) {
        var message = messageContext.lastMessage(); // message which was readed by MessageReaderStrategy

        ... // Parse the message and invoke the corresponding command handler
    }
}

```

### Create a command handler

First, create commands that you will use. Commands are just shorts values. You can create a class that holds all the commands you will use. For example:
```java
public class Commands {
    public static final short CREATE_ROOM = 0;
    public static final short JOIN_ROOM = 1;
}
```

Next, create a command handler. A command handler is a class that handles commands. It is similar to a controller in MVC. You can create a class that implements the CommandHandler interface. For example:

```java
public class MyCommandHandler implements CommandHandler {
    private ConnectionContext context;

    private final RoomFactory roomFactory; // dependency 

    @Inject
    public MyCommandHandler(RoomFactory roomFactory) {
        this.roomFactory = roomFactory;
    }

    @Override
    public void setContext(ConnectionContext context) {
        this.context = context;
    }

    @Override
    public boolean canHandle() {
        return true;
    }

    @Command(Commands.JOIN_ROOM)
    public void argTest(int a, short b, String c) {
        System.out.println("ArgTest: " + a + " " + b + " " + c);
    }
}

```

Finally, register the command handler in the builder to complete the process.

```java
builder.addCommandHandler(MyCommandHandler.class);
```

#### How commands work
When a client sends a command, the server will find a method that has the @Command annotation with the corresponding command value. For example, if a client sends a command with the value 0, the server will find a method that has the @Command annotation. 

Command invoke format:
The first two bytes of the message represent the command value, while the subsequent bytes contain the arguments for the command.

Arguemnts format:
Primitive types (byte, short, int, long, float, double, boolean) are encoded in binary format using BIGENDIAN byte order. For a string, the first 4 bytes represent the length of the string, followed by the actual string bytes. Any remaining bytes can be passed as a byte array, which is recommended to be used as the last parameter.
