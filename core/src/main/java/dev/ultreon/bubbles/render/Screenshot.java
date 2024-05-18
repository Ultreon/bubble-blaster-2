package dev.ultreon.bubbles.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.notification.Notification;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public static Screenshot take() {
        try {
            var width = graphics.getWidth();
            var height = graphics.getHeight();

            // Read framebuffer into pixmap.
            final var pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
            var pixels = pixmap.getPixels();
            Gdx.gl.glReadPixels(0, 0, width, height, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, pixels);

            // Save screenshot
            var fileHandle = BubbleBlaster.data(String.format("screenshots/screenshot_%s.png", DateTimeFormatter.ofPattern("MM.dd.yyyy-HH.mm.ss").format(LocalDateTime.now())));
            PixmapIO.writePNG(fileHandle, pixmap, Deflater.DEFAULT_COMPRESSION, true);
            pixmap.dispose();

            BubbleBlaster.getInstance().notifications.notify(
                    Notification.builder("Screenshot saved!", fileHandle.name())
                            .subText("Screenshot Manager")
                            .build()
            );
            return new Screenshot(fileHandle);
        } catch (RuntimeException e) {
            BubbleBlaster.getInstance().notifications.notify(
                    Notification.builder("Screenshot failed!", e.getMessage())
                            .subText("Screenshot Manager")
                            .build()
            );
            return null;
        }
    }

    public FileHandle fileHandle() {
        return this.fileHandle;
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
        return Objects.hash(this.fileHandle);
    }

    @Override
    public String toString() {
        return "Screenshot[" +
                "fileHandle=" + this.fileHandle + ']';
    }

}
