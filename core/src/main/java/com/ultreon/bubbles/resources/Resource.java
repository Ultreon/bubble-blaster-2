package com.ultreon.bubbles.resources;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.Hydro;
import com.ultreon.commons.function.ThrowingSupplier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Deprecated
public class Resource {
    protected ThrowingSupplier<InputStream, IOException> opener;
    private byte[] data;
    private final URL url;
    private BufferedImage image;

    public Resource(ThrowingSupplier<InputStream, IOException> opener, URL url) {
        this.opener = opener;
        this.url = url;
    }

    public void load() {
        try (InputStream inputStream = opener.get()) {
            this.data = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] loadOrGet() {
        if (data == null) {
            load();
        }

        return getData();
    }

    public InputStream loadOrOpenStream() {
        return new ByteArrayInputStream(loadOrGet());
    }

    protected Image loadImage() {
        try (InputStream inputStream = opener.get()) {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getData() {
        return data;
    }

    public ByteArrayInputStream openStream() {
        return new ByteArrayInputStream(loadOrGet());
    }

    public Font loadFont() throws FontFormatException {
        try (InputStream inputStream = opener.get()) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
//            BubbleBlaster.getInstance().registerFont(font);
            return font;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URL getUrl() {
        return url;
    }

    public BufferedImage readImage() throws IOException {
        if (this.image != null) {
            return this.image;
        }

        return this.image = ImageIO.read(openStream());
    }
}
