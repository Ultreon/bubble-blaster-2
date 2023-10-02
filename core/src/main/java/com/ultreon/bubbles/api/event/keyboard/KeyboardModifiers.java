package com.ultreon.bubbles.api.event.keyboard;

import java.util.Objects;

public record KeyboardModifiers(boolean shift, boolean ctrl, boolean alt) implements Cloneable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        KeyboardModifiers that = (KeyboardModifiers) o;
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
}
