package com.ultreon.bubbles.render.gui.cursor;

import com.ultreon.bubbles.common.Drawable;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class CursorRenderer implements Drawable {
    private final Identifier id;

    public CursorRenderer(Identifier id) {
        this.id = id;
    }

    public final Cursor create() {

        // Transparent 16 x 16 pixel cursor image.
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

        Renderer renderer = new Renderer(img.createGraphics(), BubbleBlaster.getInstance().getObserver());
        draw(renderer);

        // Create a new blank cursor.
        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(11, 11), id.toString());

        Registries.CURSORS.register(id, cursor);

        return cursor;
    }

    @Override
    public abstract void draw(Renderer renderer);

    public Identifier getId() {
        return id;
    }
}
