package org.bayasik.messages;

import org.bayasik.PerMessage;

import java.util.Arrays;

@PerMessage
public class MessageContext {
    private byte[] bytes;

    public byte[] lastMessage() {
        return bytes;
    }

    public void setLastMessage(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "MessageContext{" +
                "bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
