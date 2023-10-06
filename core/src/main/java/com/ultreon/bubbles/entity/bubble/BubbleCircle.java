package com.ultreon.bubbles.entity.bubble;


import com.ultreon.bubbles.render.Color;

import java.util.Objects;

public final class BubbleCircle {
    private final int index;
    private final Color color;

    public BubbleCircle(int index, Color color) {
        this.index = index;
        this.color = color;
    }

    public int index() {
        return this.index;
    }

    public Color color() {
        return this.color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        BubbleCircle that = (BubbleCircle) obj;
        return this.index == that.index &&
                Objects.equals(this.color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.index, this.color);
    }

    @Override
    public String toString() {
        return "BubbleCircle[" +
                "index=" + this.index + ", " +
                "color=" + this.color + ']';
    }

}
