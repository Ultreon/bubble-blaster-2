package com.ultreon.bubbles.render;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.util.List;

public class BufferRender {
    private final Renderer renderer;
    private final BufferedImage buffer;

    public BufferRender(Dimension size, ImageObserver observer) {
        size.width = Math.max(size.width, 1);
        size.height = Math.max(size.height, 1);

        BufferedImage buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        this.renderer = new Renderer(buffer.createGraphics(), observer);
        this.buffer = buffer;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public void setFilters(List<BufferedImageOp> filters) {
    }

    public BufferedImage done() {
        renderer.dispose();
        return buffer;
    }
}
