package com.ultreon.bubbles.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class Buffers {
    public static ByteBuffer read(URL url) throws IOException {
        InputStream stream = url.openStream();
        byte[] bytes = stream.readAllBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        stream.close();
        return byteBuffer;
    }

    public static byte[] readBytes(URL url) throws IOException {
        InputStream stream = url.openStream();
        byte[] bytes = stream.readAllBytes();
        stream.close();
        return bytes;
    }

    public static ByteBuffer read(InputStream stream) throws IOException {
        byte[] bytes = stream.readAllBytes();
        stream.close();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer;
    }

    public static byte[] readBytes(InputStream stream) throws IOException {
        byte[] bytes = stream.readAllBytes();
        stream.close();
        return bytes;
    }

    public static ByteBuffer wrap(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer;
    }
}
