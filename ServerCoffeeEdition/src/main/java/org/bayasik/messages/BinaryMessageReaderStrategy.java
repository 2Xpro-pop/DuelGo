package org.bayasik.messages;

import java.io.IOException;
import java.io.InputStream;

public class BinaryMessageReaderStrategy implements MessageReaderStrategy {
    @Override
    public byte[] read(InputStream inputStream) {
        try {
            if(inputStream.available() <= 4) {
                throw new RuntimeException("Message is too small");
            }

            var length = inputStream.readNBytes(4);
            var lengthInt =  (length[0] << 24) | (length[1] << 16) | (length[2] << 8) | length[3];

            var bytes = new byte[lengthInt];
            inputStream.readNBytes(bytes, 0, lengthInt);

            return bytes;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
