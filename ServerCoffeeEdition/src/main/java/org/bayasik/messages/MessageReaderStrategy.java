package org.bayasik.messages;

import java.io.InputStream;

public interface MessageReaderStrategy {
    byte[] read(InputStream inputStream);
}
