package com.ultreon.bubbles.render.screen.gui.cursor;

import java.awt.*;

public class RegistrableCursor {
    private final Cursor cursor;

    public RegistrableCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }
}
