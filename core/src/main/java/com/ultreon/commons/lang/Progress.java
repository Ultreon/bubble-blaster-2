package com.ultreon.commons.lang;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author XyperCode
 * @since 0.0.0
 */
@Deprecated
public class Progress implements Cloneable, Comparable<Progress>, Serializable {
    private int progress;
    private final int max;

    public Progress(int max) {
        this(0, max);
    }

    public Progress(int progress, int max) {
        this.progress = progress;
        this.max = max;
    }

    public void increment() {
        if (progress + 1 <= max) {
            progress++;
        } else {
            throw new IllegalStateException("Progress increment at end: " + (progress + 1) + ", max: " + max);
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getMax() {
        return max;
    }

    public float getPercentage() {
        return 100f * progress / max;
    }

    public float getRelativeProgress() {
        return (float) progress / (float) max;
    }

    private int getTodo() {
        return Math.max(max - progress, 0);
    }

    @Override
    public String toString() {
        return "Progress{" +
                "progress=" + progress +
                ", max=" + max +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Progress progress1 = (Progress) o;
        return getProgress() == progress1.getProgress() && getMax() == progress1.getMax();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProgress(), getMax());
    }

    @Override
    public int compareTo(@NotNull Progress o) {
        return 0;
    }

    @Override
    protected Progress clone() throws CloneNotSupportedException {
        return (Progress) super.clone();
    }
}
