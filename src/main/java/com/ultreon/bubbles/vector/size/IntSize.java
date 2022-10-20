package com.ultreon.bubbles.vector.size;

import org.checkerframework.common.reflection.qual.NewInstance;
import org.checkerframework.common.value.qual.IntRange;

import java.awt.*;

public record IntSize(@IntRange(from = 0) int width, @IntRange(from = 0) int height) {
    public IntSize(@IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        if (width < 0) throw new IllegalArgumentException("Width is negative");
        if (height < 0) throw new IllegalArgumentException("Height is negative");

        this.width = width;
        this.height = height;
    }

    public IntSize(Dimension size) {
        this(size.width, size.height);
    }

    @NewInstance
    public IntSize grown(int amount) {
        return new IntSize(Math.max(width + amount, 0), Math.max(height + amount, 0));
    }

    @NewInstance
    public IntSize shrunk(int amount) {
        return new IntSize(Math.max(width - amount, 0), Math.max(height - amount, 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntSize intSize = (IntSize) o;

        if (width != intSize.width) return false;
        return height == intSize.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}
