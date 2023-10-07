package com.ultreon.bubbles.entity.bubble;

import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Range {
    private final double start;
    private final double end;
    private final double step;

    public Range(double start, double end, double step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public Range(double start, double end) {
        this(start, end, 1);
    }

    public Range(double end) {
        this(0, end);
    }

    public boolean contains(double value) {
        return this.start <= value && this.end > value;
    }

    public @NotNull DoubleIterator iterator() {
        return new DoubleIterator() {
            private double current = Range.this.start;

            @Override
            public boolean hasNext() {
                return this.current < Range.this.end;
            }

            @Override
            public double nextDouble() {
                return this.current += Range.this.step;
            }
        };
    }

    public DoubleIterable iterable() {
        return Range.this::iterator;
    }

    public double start() {
        return this.start;
    }

    public double end() {
        return this.end;
    }

    public double step() {
        return this.step;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Range) obj;
        return Double.doubleToLongBits(this.start) == Double.doubleToLongBits(that.start) &&
                Double.doubleToLongBits(this.end) == Double.doubleToLongBits(that.end) &&
                Double.doubleToLongBits(this.step) == Double.doubleToLongBits(that.step);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.start, this.end, this.step);
    }

    @Override
    public String toString() {
        return "Range[" +
                "start=" + this.start + ", " +
                "end=" + this.end + ", " +
                "step=" + this.step + ']';
    }

}
