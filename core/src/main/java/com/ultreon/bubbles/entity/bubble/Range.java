package com.ultreon.bubbles.entity.bubble;

import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import org.jetbrains.annotations.NotNull;

public record Range(double start, double end, double step) {
    public Range(double start, double end) {
        this(start, end, 1);
    }

    public Range(double end) {
        this(0, end);
    }

    public boolean contains(double value) {
        return (start <= value) && (end > value);
    }

    public @NotNull DoubleIterator iterator() {
        return new DoubleIterator() {
            private double current = start;

            @Override
            public boolean hasNext() {
                return current < end;
            }

            @Override
            public double nextDouble() {
                return current += step;
            }
        };
    }

    public DoubleIterable iterable() {
        return Range.this::iterator;
    }
}
