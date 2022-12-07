package com.ultreon.bubbles.resources;

import com.ultreon.commons.function.ThrowingSupplier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

public class ImageResource extends Resource {
    private BufferedImage image;

    public ImageResource(ThrowingSupplier<InputStream, IOException> opener) {
        super(opener);
    }

    @Override
    public void load() {
        try (InputStream inputStream = opener.get()) {
            this.image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public BufferedImage getImage() {
        return image;
    }
}
