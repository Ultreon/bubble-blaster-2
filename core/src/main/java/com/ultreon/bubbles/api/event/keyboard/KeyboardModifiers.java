package com.ultreon.bubbles.api.event.keyboard;

import java.util.Objects;

public final class KeyboardModifiers implements Cloneable {
    private final boolean shift;
    private final boolean ctrl;
    private final boolean alt;

    public KeyboardModifiers(boolean shift, boolean ctrl, boolean alt) {
        this.shift = shift;
        this.ctrl = ctrl;
        this.alt = alt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        var that = (KeyboardModifiers) o;
        return this.shift == that.shift && this.ctrl == that.ctrl && this.alt == that.alt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.shift, this.ctrl, this.alt);
    }

    @Override
    public String toString() {
        return "KeyboardModifiers{" +
                "shift=" + this.shift +
                ", ctrl=" + this.ctrl +
                ", alt=" + this.alt +
                '}';
    }

    @Override
    protected KeyboardModifiers clone() {
        try {
            return (KeyboardModifiers) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError("Should be cloneable because object implements cloneable interface.");
        }
    }

    public boolean shift() {
        return this.shift;
    }

    public boolean ctrl() {
        return this.ctrl;
    }

    public boolean alt() {
        return this.alt;
    }

}
