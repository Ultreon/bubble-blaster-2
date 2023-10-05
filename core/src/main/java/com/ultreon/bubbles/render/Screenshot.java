package com.ultreon.bubbles.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.ultreon.bubbles.BubbleBlaster;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.zip.Deflater;

import static com.badlogic.gdx.Gdx.graphics;

public final class Screenshot {
    private final FileHandle fileHandle;

    public Screenshot(FileHandle fileHandle) {
        this.fileHandle = fileHandle;
    }

    public static Screenshot take() {
        int width = graphics.getWidth();
        int height = graphics.getHeight();

        // Read framebuffer into pixmap.
        final Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
        ByteBuffer pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(0, 0, width, height, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, pixels);

        // Save screenshot
        FileHandle fileHandle = BubbleBlaster.data(String.format("screenshots/screenshot_%s.png", DateTimeFormatter.ofPattern("MM.dd.yyyy-HH.mm.ss").format(LocalDateTime.now())));
        PixmapIO.writePNG(fileHandle, pixmap, Deflater.DEFAULT_COMPRESSION, true);
        pixmap.dispose();
        return new Screenshot(fileHandle);
    }

    public FileHandle fileHandle() {
        return fileHandle;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Screenshot) obj;
        return Objects.equals(this.fileHandle, that.fileHandle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileHandle);
    }

    @Override
    public String toString() {
        return "Screenshot[" +
                "fileHandle=" + fileHandle + ']';
    }

}
