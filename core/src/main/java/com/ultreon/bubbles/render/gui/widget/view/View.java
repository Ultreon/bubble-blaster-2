package com.ultreon.bubbles.render.gui.widget.view;

import com.ultreon.bubbles.render.gui.GuiComponent;
import org.checkerframework.common.value.qual.IntRange;

public abstract class View extends GuiComponent {
    public View(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }
}
