package com.ultreon.bubbles.render.screen.gui.cursor;

import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

public class DefaultCursorRenderer extends CursorRenderer {
    public DefaultCursorRenderer() {
        super("default_cursor");
    }

    @Override
    public void draw(Renderer renderer) {
        Polygon poly = new Polygon(new int[]{0, 10, 5, 0}, new int[]{0, 12, 12, 16}, 4);

        renderer.hint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderer.color(Color.black);
        renderer.polygon(poly);
        renderer.color(Color.white);
        renderer.polygonLine(poly);
        renderer.dispose();
    }
}
