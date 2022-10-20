package com.ultreon.bubbles.vector;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

@SuppressWarnings("unused")
public class Vec3f implements Externalizable, Cloneable {
    public float x, y, z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f() {

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

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public static Vec3f mul(Vec3f a, Vec3f b) {
        return new Vec3f(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public static Vec3f div(Vec3f a, Vec3f b) {
        return new Vec3f(a.x / b.x, a.y / b.y, a.z / b.z);
    }

    public static Vec3f add(Vec3f a, Vec3f b) {
        return new Vec3f(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vec3f sub(Vec3f a, Vec3f b) {
        return new Vec3f(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static float dot(Vec3f a, Vec3f b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vec3d pow(Vec3f a, Vec3f b) {
        return new Vec3d(Math.pow(a.x, b.x), Math.pow(a.y, b.y), Math.pow(a.z, b.z));
    }

    public Vec3d d() {
        return new Vec3d(x, y, z);
    }

    public Vec3f f() {
        return new Vec3f(x, y, z);
    }

    public Vec3i i() {
        return new Vec3i((int) x, (int) y, (int) z);
    }

    @Override
    public Vec3f clone() {
        try {
            Vec3f clone = (Vec3f) super.clone();

            clone.x = this.x;
            clone.y = this.y;
            clone.z = this.z;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec3f vector4i = (Vec3f) o;
        return getX() == vector4i.getX() && getY() == vector4i.getY() && getZ() == vector4i.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ());
    }

    @Override
    public String toString() {
        return "Vector4i{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(z);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.z = in.readFloat();
    }
}
