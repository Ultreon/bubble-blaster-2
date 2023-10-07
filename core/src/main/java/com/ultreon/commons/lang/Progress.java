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
        if (this.progress + 1 <= this.max) {
            this.progress++;
        } else {
            throw new IllegalStateException("Progress increment at end: " + (this.progress + 1) + ", max: " + this.max);
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMax() {
        return this.max;
    }

    public float getPercentage() {
        return 100f * this.progress / this.max;
    }

    public float getRelativeProgress() {
        return (float) this.progress / (float) this.max;
    }

    private int getTodo() {
        return Math.max(this.max - this.progress, 0);
    }

    @Override
    public String toString() {
        return "Progress{" +
                "progress=" + this.progress +
                ", max=" + this.max +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        var progress1 = (Progress) o;
        return this.getProgress() == progress1.getProgress() && this.getMax() == progress1.getMax();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getProgress(), this.getMax());
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
