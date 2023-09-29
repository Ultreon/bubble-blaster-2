package com.ultreon.bubbles.render;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Insets implements Cloneable, Serializable {
    public int top;
    public int left;
    public int bottom;
    public int right;

    @Serial
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
        top -= amount;
        left -= amount;
        bottom -= amount;
        right -= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insets insets = (Insets) o;
        return top == insets.top && left == insets.left && bottom == insets.bottom && right == insets.right;
    }

    @Override
    public int hashCode() {
        return Objects.hash(top, left, bottom, right);
    }

    @Override
    public String toString() {
        return "Insets{" +
                "top=" + top +
                ", left=" + left +
                ", bottom=" + bottom +
                ", right=" + right +
                '}';
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new InternalError(var2);
        }
    }
}
