package com.ultreon.bubbles.render.gui.cursor;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;


public class TextCursorRenderer extends CursorRenderer {
    public TextCursorRenderer() {
        super(BubbleBlaster.id("text_cursor"));
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.hint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderer.color(Color.white);
        renderer.line(0, 1, 0, 24);
        renderer.color(Color.white);
        renderer.line(1, 0, 1, 25);
        renderer.color(Color.white);
        renderer.line(2, 1, 2, 24);
        renderer.color(Color.black);
        renderer.line(1, 1, 1, 24);
        renderer.dispose();
    }
}
