package com.ultreon.test.bubbles;

import com.ultreon.bubbles.render.Renderer;

import java.awt.*;
import java.awt.image.ImageProducer;

public class ImageCanvas extends Canvas {
    Image image;

    public ImageCanvas(String name) {
        MediaTracker media = new MediaTracker(this);
        image = Toolkit.getDefaultToolkit().getImage(name);
        media.addImage(image, 0);
        try {
            media.waitForID(0);
        } catch (Exception ignored) {
        }
    }

    public ImageCanvas(ImageProducer imageProducer) {
        image = createImage(imageProducer);
    }

    public void paint(Renderer renderer) {
        renderer.image(image, 0, 0);
    }

    public static void main(String[] argv) {
        if (argv.length < 1) {
            System.out.println("usage: ImageCanvas.class [image file name]");
            System.exit(0);
        }
        Frame frame = new Frame(argv[0]);
        frame.setLayout(new BorderLayout());
        frame.add("Center", new ImageCanvas(argv[0]));
        frame.setSize(400, 400);
        frame.pack();
        frame.setVisible(true);
    }
}