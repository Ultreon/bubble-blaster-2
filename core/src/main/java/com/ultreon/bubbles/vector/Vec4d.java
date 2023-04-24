package com.ultreon.bubbles.vector;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

@SuppressWarnings("unused")
public class Vec4d implements Externalizable, Cloneable {
    public double x, y, z, w;

    public Vec4d(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4d() {

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public static Vec4d mul(Vec4d a, Vec4d b) {
        return new Vec4d(a.x * b.x, a.y * b.y, a.z * b.z, a.w * b.w);
    }

    public static Vec4d div(Vec4d a, Vec4d b) {
        return new Vec4d(a.x / b.x, a.y / b.y, a.z / b.z, a.w / b.w);
    }

    public static Vec4d add(Vec4d a, Vec4d b) {
        return new Vec4d(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
    }

    public static Vec4d sub(Vec4d a, Vec4d b) {
        return new Vec4d(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
    }

    public static double dot(Vec4d a, Vec4d b) {
        return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
    }

    public static Vec4d pow(Vec4d a, Vec4d b) {
        return new Vec4d(Math.pow(a.x, b.x), Math.pow(a.y, b.y), Math.pow(a.z, b.z), Math.pow(a.w, b.w));
    }

    public Vec4d d() {
        return new Vec4d(x, y, z, w);
    }

    public Vec4f f() {
        return new Vec4f((float) x, (float) y, (float) z, (float) w);
    }

    public Vec4i i() {
        return new Vec4i((int) x, (int) y, (int) z, (int) w);
    }

    @Override
    public Vec4d clone() {
        try {
            Vec4d clone = (Vec4d) super.clone();

            clone.x = this.x;
            clone.y = this.y;
            clone.z = this.z;
            clone.w = this.w;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec4d vector4i = (Vec4d) o;
        return getX() == vector4i.getX() && getY() == vector4i.getY() && getZ() == vector4i.getZ() && getW() == vector4i.getW();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ(), getW());
    }

    @Override
    public String toString() {
        return "Vector4i{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeDouble(w);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.w = in.readDouble();
    }
}
