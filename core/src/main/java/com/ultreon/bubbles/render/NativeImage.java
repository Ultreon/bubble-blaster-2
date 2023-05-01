package com.ultreon.bubbles.render;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.ultreon.libs.resources.v0.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class NativeImage extends Texture {
    public NativeImage(byte[] bytes) {
        super(new Pixmap(bytes, 0, bytes.length));
    }

    public NativeImage(Resource resource) {
        this(resource.loadOrGet());
    }

    public NativeImage(InputStream stream) throws IOException {
        super(new Pixmap(read(stream)));
    }

    public NativeImage(URL url) throws IOException {
        super(new Pixmap(readAndClose(url.openStream())));
    }

    public NativeImage(File file) throws IOException {
        super(new Pixmap(readAndClose(new FileInputStream(file))));
    }

    private static ByteBuffer readAndClose(InputStream inputStream) throws IOException {
        ByteBuffer read = read(inputStream);
        inputStream.close();
        return read;
    }

    private static ByteBuffer read(InputStream inputStream) throws IOException {
        return ByteBuffer.wrap(inputStream.readAllBytes());
    }
}