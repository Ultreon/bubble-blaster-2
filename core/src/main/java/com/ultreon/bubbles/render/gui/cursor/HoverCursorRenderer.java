package com.ultreon.bubbles.render.gui.cursor;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

public class HoverCursorRenderer extends CursorRenderer {
    public HoverCursorRenderer() {
        super(BubbleBlaster.id("pointer_cursor"));
    }

    @Override
    public void draw(Renderer renderer) {
        Polygon poly = new Polygon(new int[]{10, 20, 15, 10}, new int[]{10, 22, 22, 26}, 4);

        renderer.hint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderer.setColor(Color.white);
        renderer.ovalLine(0, 0, 20, 20);
        renderer.setColor(Color.white);
        renderer.ovalLine(2, 2, 16, 16);
        renderer.setColor(Color.black);
        renderer.polygon(poly);
        renderer.setColor(Color.white);
        renderer.polygonLine(poly);
        renderer.setColor(Color.black);
        renderer.ovalLine(1, 1, 18, 18);
        renderer.dispose();

    }
}
