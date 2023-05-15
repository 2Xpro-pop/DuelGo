package org.bayasik.commands;

import org.bayasik.connection.ConnectionContext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandsExplorer {
    private final CommandDescriptor[] commandDescriptors;
    private final ConnectionContext connectionContext;

    public CommandsExplorer(CommandDescriptor[] commandDescriptors, ConnectionContext connectionContext) {
        this.commandDescriptors = commandDescriptors;
        this.connectionContext = connectionContext;
    }

    public CommandToken readCommand(byte[] bytes) {
        var commandId = new byte[] { bytes[0], bytes[1] };
        var commandData = new byte[bytes.length - 2];

        System.arraycopy(bytes, 2, commandData, 0, commandData.length);

        return new CommandToken((short) ((commandId[0] << 8) | commandId[1]), commandData);
    }

    public CommandDescriptor getCommandDescriptor(CommandToken commandToken) {
        for (var commandDescriptor : commandDescriptors) {
            if (commandDescriptor.commandId == commandToken.commandId()) {
                if(commandDescriptor.getInstance() != null && !commandDescriptor.getInstance().canHandle()) continue;
                return commandDescriptor;
            }
        }
        return null;
    }

    public CommandDescriptor getCommandDescriptor(short commandId) {
        for (var commandDescriptor : commandDescriptors) {
            if (commandDescriptor.commandId == commandId) {
                if(commandDescriptor.getInstance() != null && !commandDescriptor.getInstance().canHandle()) continue;
                return commandDescriptor;
            }
        }
        return null;
    }

    public void InvokeCommand(CommandToken commandToken) {
        var commandDescriptor = getCommandDescriptor(commandToken);
        if (commandDescriptor == null) {
            throw new RuntimeException("Command not found");
        }

        if(commandDescriptor.isAnonymous()){
            commandDescriptor.anonymousCommand.handle(connectionContext);
            return;
        }

        if(commandDescriptor.getInstance() == null){
            throw new RuntimeException("Command instance not found");
        }



        InvokeWithParameters(commandDescriptor.getInstance(), commandDescriptor.method, commandToken.data());
    }

    private void InvokeWithParameters(CommandHandler instance, Method method, byte[] data){
        var parameters = method.getParameters();
        var args = new Object[parameters.length];
        var lastParamIndex = 0;

        for (int i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            var type = parameter.getType();
            if(type == byte.class){
                args[i] = data[lastParamIndex++];
            }
            if(type == short.class){
                args[i] = (short) ((data[lastParamIndex++] << 8) | data[lastParamIndex++]);
            }
            if(type == int.class){
                args[i] = (int) ((data[lastParamIndex++] << 24) | (data[lastParamIndex++] << 16) | (data[lastParamIndex++] << 8) | data[lastParamIndex++]);
            }
            if(type == long.class){
                args[i] = (long) ((data[lastParamIndex++] << 56) | (data[lastParamIndex++] << 48) | (data[lastParamIndex++] << 40) | (data[lastParamIndex++] << 32) | (data[lastParamIndex++] << 24) | (data[lastParamIndex++] << 16) | (data[lastParamIndex++] << 8) | data[lastParamIndex++]);
            }
            if(type == float.class){
                args[i] = Float.intBitsToFloat((int) ((data[lastParamIndex++] << 24) | (data[lastParamIndex++] << 16) | (data[lastParamIndex++] << 8) | data[lastParamIndex++]));
            }
            if(type == double.class){
                args[i] = Double.longBitsToDouble((long) ((data[lastParamIndex++] << 56) | (data[lastParamIndex++] << 48) | (data[lastParamIndex++] << 40) | (data[lastParamIndex++] << 32) | (data[lastParamIndex++] << 24) | (data[lastParamIndex++] << 16) | (data[lastParamIndex++] << 8) | data[lastParamIndex++]));
            }
            if(type == boolean.class){
                args[i] = data[lastParamIndex++] == 1;
            }
            if(type == byte[].class){
                var lastBytes = new byte[data.length - lastParamIndex];
                System.arraycopy(data, lastParamIndex, lastBytes, 0, lastBytes.length);
                args[i] = lastBytes;
            }
            if(type == String.class){
                var vm = readString(data, lastParamIndex);
                args[i] = vm.instance;
                lastParamIndex = vm.index;
            }
        }

        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private DataVm<String> readString(byte[] data, int index){
        var length = (int) ((data[index++] << 24) | (data[index++] << 16) | (data[index++] << 8) | data[index++]);
        var stringBytes = new byte[length];
        System.arraycopy(data, index, stringBytes, 0, length);
        return new DataVm<>(new String(stringBytes), index);
    }

    private class DataVm<T>{
        public final T instance;
        public final int index;

        DataVm(T instance, int index) {
            this.instance = instance;
            this.index = index;
        }
    }
}
