package com.ultreon.bubbles.render.screen.gui.cursor;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.common.Drawable;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class CursorRenderer implements Drawable {
    private final String name;

    public CursorRenderer(String name) {
        this.name = name;
    }

    public final Cursor create() {

        // Transparent 16 x 16 pixel cursor image.
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

        Renderer renderer = new Renderer(img.createGraphics(), BubbleBlaster.getInstance().getObserver());
        draw(renderer);

        // Create a new blank cursor.
        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(
                img, new Point(11, 11), name);

        Registry.getRegistry(RegistrableCursor.class).register(Identifier.parse("qbubbles:" + name), new RegistrableCursor(cursor));

        return cursor;
    }

    @Override
    public abstract void draw(Renderer renderer);

    public String getName() {
        return name;
    }
}
