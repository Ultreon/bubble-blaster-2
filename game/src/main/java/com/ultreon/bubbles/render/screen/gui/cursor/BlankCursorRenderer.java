package com.ultreon.bubbles.render.screen.gui.cursor;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Renderer;

public class BlankCursorRenderer extends CursorRenderer {
    public BlankCursorRenderer() {
        super(BubbleBlaster.id("blank_cursor"));
    }

    @Override
    public void draw(Renderer renderer) {

    }
}
