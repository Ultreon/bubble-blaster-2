package com.ultreon.bubbles.event.v1.window;

import com.ultreon.bubbles.game.GameWindow;
import com.ultreon.bubbles.vector.Vec2f;
import com.ultreon.bubbles.vector.Vec2i;

@Deprecated
public class WindowResizeEvent extends WindowEvent {
    private final Vec2i oldSize;
    private final Vec2f newSize;

    public WindowResizeEvent(GameWindow window, Vec2i oldSize, Vec2f newSize) {
        super(window);
        this.oldSize = oldSize;
        this.newSize = newSize;
    }

    public Vec2i getOldSize() {
        return oldSize;
    }

    public Vec2f getNewSize() {
        return newSize;
    }
}
