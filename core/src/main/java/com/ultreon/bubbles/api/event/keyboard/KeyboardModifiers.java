package com.ultreon.bubbles.api.event.keyboard;

import java.util.Objects;

public record KeyboardModifiers(boolean shift, boolean ctrl, boolean alt) implements Cloneable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyboardModifiers that = (KeyboardModifiers) o;
        return shift == that.shift && ctrl == that.ctrl && alt == that.alt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shift, ctrl, alt);
    }

    @Override
    public String toString() {
        return "KeyboardModifiers{" +
                "shift=" + shift +
                ", ctrl=" + ctrl +
                ", alt=" + alt +
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
