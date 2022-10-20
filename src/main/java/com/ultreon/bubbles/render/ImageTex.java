package com.ultreon.bubbles.render;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public abstract class ImageTex extends Texture {
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
    public void render(Renderer renderer, int xf, int yf, int xs, int ys) {
        renderer.image(image, xf, yf, xs - xf, ys - yf);
    }
}
