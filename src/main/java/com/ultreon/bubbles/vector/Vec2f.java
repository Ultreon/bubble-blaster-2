package com.ultreon.bubbles.vector;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

@SuppressWarnings("unused")
public class Vec2f implements Externalizable, Cloneable {
    public float x, y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f() {

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void mul(float i) {
        x *= i;
        y *= i;
    }

    public void div(float i) {
        x /= i;
        y /= i;
    }

    public void add(float i) {
        x += i;
        y += i;
    }

    public void sub(float i) {
        x -= i;
        y -= i;
    }

    public void pow(float i) {
        x = (float) Math.pow(x, i);
        y = (float) Math.pow(y, i);
    }

    public void mul(float x, float y) {
        this.x *= x;
        this.y *= y;
    }

    public void div(float x, float y) {
        this.x /= x;
        this.y /= y;
    }

    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void sub(float x, float y) {
        this.x -= x;
        this.y -= y;
    }

    public void pow(float x, float y) {
        this.x = (float) Math.pow(this.x, x);
        this.y = (float) Math.pow(this.y, y);
    }

    public static Vec2f mul(Vec2f a, Vec2f b) {
        return new Vec2f(a.x * b.x, a.y * b.y);
    }

    public static Vec2f div(Vec2f a, Vec2f b) {
        return new Vec2f(a.x / b.x, a.y / b.y);
    }

    public static Vec2f add(Vec2f a, Vec2f b) {
        return new Vec2f(a.x + b.x, a.y + b.y);
    }

    public static Vec2f sub(Vec2f a, Vec2f b) {
        return new Vec2f(a.x - b.x, a.y - b.y);
    }

    public static float dot(Vec2f a, Vec2f b) {
        return a.x * b.x + a.y * b.y;
    }

    public static Vec2d pow(Vec2f a, Vec2f b) {
        return new Vec2d(Math.pow(a.x, b.x), Math.pow(a.y, b.y));
    }

    public Vec2d d() {
        return new Vec2d(x, y);
    }

    public Vec2f f() {
        return new Vec2f(x, y);
    }

    public Vec2i i() {
        return new Vec2i((int) x, (int) y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2f vec2F = (Vec2f) o;
        return Float.compare(vec2F.getX(), getX()) == 0 && Float.compare(vec2F.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "Vector2f{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public Vec2f clone() {
        try {
            Vec2f clone = (Vec2f) super.clone();

            clone.x = this.x;
            clone.y = this.y;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.x = in.readFloat();
        this.y = in.readFloat();
    }
}
