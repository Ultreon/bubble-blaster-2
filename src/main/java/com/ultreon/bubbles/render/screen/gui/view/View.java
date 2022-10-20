package com.ultreon.bubbles.render.screen.gui.view;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.gui.InputWidget;
import org.checkerframework.common.value.qual.IntRange;

public abstract class View extends InputWidget {
    public Renderer containerGraphics;

    public View(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }
}
