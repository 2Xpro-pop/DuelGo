package org.bayasik.commands;

import java.util.Arrays;

public record CommandToken(short commandId, byte[] data) {

    @Override
    public String toString() {
        return "CommandToken{" +
                "commandId=" + commandId +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
