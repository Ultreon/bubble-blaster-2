package com.ultreon.bubbles.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class Buffers {
    public static ByteBuffer read(URL url) throws IOException {
        var stream = url.openStream();
        var bytes = stream.readAllBytes();
        var byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        stream.close();
        return byteBuffer;
    }

    public static byte[] readBytes(URL url) throws IOException {
        var stream = url.openStream();
        var bytes = stream.readAllBytes();
        stream.close();
        return bytes;
    }

    public static ByteBuffer read(InputStream stream) throws IOException {
        var bytes = stream.readAllBytes();
        stream.close();
        var byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer;
    }

    public static byte[] readBytes(InputStream stream) throws IOException {
        var bytes = stream.readAllBytes();
        stream.close();
        return bytes;
    }

    public static ByteBuffer wrap(byte[] bytes) {
        var byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer;
    }
}
