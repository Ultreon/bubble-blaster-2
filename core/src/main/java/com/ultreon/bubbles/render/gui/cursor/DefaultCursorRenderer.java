package com.ultreon.bubbles.render.gui.cursor;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

public class DefaultCursorRenderer extends CursorRenderer {
    public DefaultCursorRenderer() {
        super(BubbleBlaster.id("default_cursor"));
    }

    @Override
    public void draw(Renderer renderer) {
        Polygon poly = new Polygon(new int[]{0, 10, 5, 0}, new int[]{0, 12, 12, 16}, 4);

        renderer.hint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderer.setColor(Color.black);
        renderer.polygon(poly);
        renderer.setColor(Color.white);
        renderer.polygonLine(poly);
        renderer.dispose();
    }
}
