package com.ultreon.bubbles.vector.size;

import org.checkerframework.common.reflection.qual.NewInstance;
import org.checkerframework.common.value.qual.IntRange;

public record FloatSize(@IntRange(from = 0) float width, @IntRange(from = 0) float height) {
    public FloatSize(@IntRange(from = 0) float width, @IntRange(from = 0) float height) {
        if (width < 0) throw new IllegalArgumentException("Width is negative");
        if (height < 0) throw new IllegalArgumentException("Height is negative");

        this.width = width;
        this.height = height;
    }

    @NewInstance
    public FloatSize grown(float amount) {
        return new FloatSize(Math.max(width + amount, 0), Math.max(height + amount, 0));
    }

    @NewInstance
    public FloatSize shrunk(float amount) {
        return new FloatSize(Math.max(width - amount, 0), Math.max(height - amount, 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FloatSize floatSize = (FloatSize) o;

        if (Float.compare(floatSize.width, width) != 0) return false;
        return Float.compare(floatSize.height, height) == 0;
    }

    @Override
    public int hashCode() {
        int result = (width != 0.0f ? Float.floatToIntBits(width) : 0);
        result = 31 * result + (height != 0.0f ? Float.floatToIntBits(height) : 0);
        return result;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}
