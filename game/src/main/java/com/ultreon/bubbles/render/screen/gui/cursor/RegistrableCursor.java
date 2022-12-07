package com.ultreon.bubbles.render.screen.gui.cursor;

import com.ultreon.bubbles.common.Registrable;

import java.awt.*;

public class RegistrableCursor extends Registrable {
    private final Cursor cursor;

    public RegistrableCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }
}
