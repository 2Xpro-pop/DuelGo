package org.bayasik.commands;

import java.io.IOException;
import java.io.InputStream;

public class CommandsExplorer {
    private final CommandDescriptor[] commandDescriptors;

    public CommandsExplorer(CommandDescriptor[] commandDescriptors) {
        this.commandDescriptors = commandDescriptors;
    }

    public CommandToken readCommand(InputStream inputStream) throws IOException {
        if(inputStream.readNBytes(1)[0] == 0x00) {
            var commandId = inputStream.readNBytes(2);
            var commandData = inputStream.readAllBytes();

            return new CommandToken((short) ((commandId[0] << 8) | commandId[1]), commandData);
        }
        return null;
    }
}
