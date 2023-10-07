package com.ultreon.bubbles.render;

import java.io.Serializable;
import java.util.Objects;

public class Insets implements Cloneable, Serializable {
    public int top;
    public int left;
    public int bottom;
    public int right;

    private static final long serialVersionUID = 1041450873844924442L;

    public Insets() {
        this(1);
    }

    public Insets(int all) {
        this(all, all);
    }

    public Insets(int horizontal, int vertical) {
        this(vertical, horizontal, vertical, horizontal);
    }

    public Insets(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public void set(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public void shrink(int amount) {
        this.top -= amount;
        this.left -= amount;
        this.bottom -= amount;
        this.right -= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        var insets = (Insets) o;
        return this.top == insets.top && this.left == insets.left && this.bottom == insets.bottom && this.right == insets.right;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.top, this.left, this.bottom, this.right);
    }

    @Override
    public String toString() {
        return "Insets{" +
                "top=" + this.top +
                ", left=" + this.left +
                ", bottom=" + this.bottom +
                ", right=" + this.right +
                '}';
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new InternalError(var2);
        }
    }
}
