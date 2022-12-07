package com.ultreon.bubbles.vector;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

@SuppressWarnings("unused")
public class Vec3i implements Externalizable, Cloneable {
    public int x, y, z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i() {

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public static Vec3i mul(Vec3i a, Vec3i b) {
        return new Vec3i(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public static Vec3i div(Vec3i a, Vec3i b) {
        return new Vec3i(a.x / b.x, a.y / b.y, a.z / b.z);
    }

    public static Vec3i add(Vec3i a, Vec3i b) {
        return new Vec3i(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vec3i sub(Vec3i a, Vec3i b) {
        return new Vec3i(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static int dot(Vec3i a, Vec3i b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vec3d pow(Vec3i a, Vec3i b) {
        return new Vec3d(Math.pow(a.x, b.x), Math.pow(a.y, b.y), Math.pow(a.z, b.z));
    }

    public Vec3d d() {
        return new Vec3d(x, y, z);
    }

    public Vec3f f() {
        return new Vec3f(x, y, z);
    }

    public Vec3i i() {
        return new Vec3i(x, y, z);
    }

    @Override
    public Vec3i clone() {
        try {
            Vec3i clone = (Vec3i) super.clone();

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
        Vec3i vector4i = (Vec3i) o;
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
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(z);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.x = in.readInt();
        this.y = in.readInt();
        this.z = in.readInt();
    }
}
