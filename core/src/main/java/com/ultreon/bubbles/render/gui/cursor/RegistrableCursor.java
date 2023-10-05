package com.ultreon.bubbles.render.gui.cursor;


import com.badlogic.gdx.graphics.Cursor;

import java.util.Objects;

public final class RegistrableCursor {
    private final Cursor cursor;

    public RegistrableCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Cursor cursor() {
        return cursor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RegistrableCursor) obj;
        return Objects.equals(this.cursor, that.cursor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursor);
    }

    @Override
    public String toString() {
        return "RegistrableCursor[" +
                "cursor=" + cursor + ']';
    }

}
