package com.ultreon.bubbles.render;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public abstract class AwtImage extends Texture {
    BufferedImage image;

    protected abstract byte[] loadBytes();

    public void load() {
        byte[] bytes = loadBytes();
        try {
            this.image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void draw(Renderer renderer, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight) {
        int texWidth = image.getWidth();
        int texHeight = image.getHeight();
        image.getSubimage(u, v, uWidth, vHeight);
        renderer.image(image, x, y, width, height);
    }

    @Override
    public Raster getRaster() {
        return image.getRaster();
    }

    @Override
    protected int getWidth() {
        return image.getWidth();
    }

    @Override
    protected int getHeight() {
        return image.getHeight();
    }
}
