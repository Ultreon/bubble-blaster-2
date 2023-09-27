package com.ultreon.bubbles.util;

import com.badlogic.gdx.files.FileHandle;
import com.ultreon.bubbles.resources.ByteArrayFileHandle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileHandles {
    public static FileHandle imageBytes(URL url) throws IOException {
        return new ByteArrayFileHandle(".png", Buffers.readBytes(url));
    }

    public static FileHandle imageBytes(InputStream stream) throws IOException {
        return new ByteArrayFileHandle(".png", Buffers.readBytes(stream));
    }

    public static FileHandle imageBytes(byte[] bytes) throws IOException {
        return new ByteArrayFileHandle(".png", bytes);
    }
}
