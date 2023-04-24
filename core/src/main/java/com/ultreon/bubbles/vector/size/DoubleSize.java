package com.ultreon.bubbles.vector.size;

import org.checkerframework.common.reflection.qual.NewInstance;
import org.checkerframework.common.value.qual.IntRange;

public record DoubleSize(@IntRange(from = 0) double width, @IntRange(from = 0) double height) {
    public DoubleSize(@IntRange(from = 0) double width, @IntRange(from = 0) double height) {
        if (width < 0) throw new IllegalArgumentException("Width is negative");
        if (height < 0) throw new IllegalArgumentException("Height is negative");

        this.width = width;
        this.height = height;
    }

    @NewInstance
    public DoubleSize grown(double amount) {
        return new DoubleSize(Math.max(width + amount, 0), Math.max(height + amount, 0));
    }

    @NewInstance
    public DoubleSize shrunk(double amount) {
        return new DoubleSize(Math.max(width - amount, 0), Math.max(height - amount, 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleSize that = (DoubleSize) o;

        if (Double.compare(that.width, width) != 0) return false;
        return Double.compare(that.height, height) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(width);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(height);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}
