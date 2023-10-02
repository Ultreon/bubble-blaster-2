package com.ultreon.bubbles.render;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.ultreon.bubbles.BubbleBlaster;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.Deflater;

import static com.badlogic.gdx.Gdx.graphics;

public record Screenshot(FileHandle fileHandle) {
    public static Screenshot take() {
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, graphics.getWidth(), graphics.getHeight());
        FileHandle fileHandle = BubbleBlaster.data(String.format("screenshots/screenshot_%s.png", DateTimeFormatter.ofPattern("MM.dd.yyyy-HH.mm.ss").format(LocalDateTime.now())));
        PixmapIO.writePNG(fileHandle, pixmap, Deflater.DEFAULT_COMPRESSION, true);
        pixmap.dispose();
        return new Screenshot(fileHandle);
    }
}
