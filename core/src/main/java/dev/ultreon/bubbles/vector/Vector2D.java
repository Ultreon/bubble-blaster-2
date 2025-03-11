package dev.ultreon.bubbles.vector;

import com.badlogic.gdx.math.MathUtils;

public class Vector2D implements VectorD<Vector2D> {
    public double x;
    public double y;

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2D set(Vector2D vector) {
        this.x = vector.x;
        this.y = vector.y;
        return this;
    }

    public Vector2D scl(double value) {
        this.x *= value;
        this.y *= value;
        return this;
    }

    public Vector2D add(double value) {
        this.x += value;
        this.y += value;
        return this;
    }

    public Vector2D add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2D sub(double value) {
        this.x -= value;
        this.y -= value;
        return this;
    }

    public Vector2D sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2D div(double value) {
        this.x /= value;
        this.y /= value;
        return this;
    }

    public Vector2D div(double x, double y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    @Override
    public Vector2D scl(Vector2D value) {
        this.x *= value.x;
        this.y *= value.y;
        return this;
    }

    @Override
    public Vector2D add(Vector2D value) {
        this.x += value.x;
        this.y += value.y;
        return this;
    }

    @Override
    public Vector2D sub(Vector2D value) {
        this.x -= value.x;
        this.y -= value.y;
        return this;
    }

    @Override
    public Vector2D div(Vector2D value) {
        this.x /= value.x;
        this.y /= value.y;
        return this;
    }

    public double dst(Vector2D vector) {
        return Math.sqrt((vector.x - this.x) * (vector.x - this.x) + (vector.y - this.y) * (vector.y - this.y));
    }

    public double dst(double x, double y) {
        return Math.sqrt((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y));
    }

    public double len() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vector2D nor() {
        var len = this.len();
        this.x /= len;
        this.y /= len;
        return this;
    }

    public double dot(Vector2D v) {
        return this.x * v.x + this.y * v.y;
    }

    public double dot(double x, double y) {
        return this.x * x + this.y * y;
    }

    public double crs(Vector2D v) {
        return this.x * v.y - this.y * v.x;
    }

    public double crs(double x, double y) {
        return this.x * y - this.y * x;
    }

    public Vector2D cpy() {
        return new Vector2D(this.x, this.y);
    }

    public float angleDeg() {
        var angle = (float) Math.atan2(this.y, this.x) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        return angle;
    }

    public float angleDeg(Vector2D reference) {
        var angle = (float) Math.atan2(reference.crs(this), reference.dot(this)) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        return angle;
    }

    public Vector2D setAngleDeg(float degrees) {
        return this.setAngleRad(degrees * MathUtils.degreesToRadians);
    }

    public Vector2D setAngleRad(float radians) {
        this.set(this.len(), 0f);
        this.rotateRad(radians);

        return this;
    }

    public Vector2D rotateRad(float radians) {
        var cos = Math.cos(radians);
        var sin = Math.sin(radians);

        var newX = this.x * cos - this.y * sin;
        var newY = this.x * sin + this.y * cos;

        this.x = newX;
        this.y = newY;

        return this;
    }

    public Vector2D lerp(Vector2D target, double alpha) {
        var invAlpha = 1.0f - alpha;
        this.x = (this.x * invAlpha) + (target.x * alpha);
        this.y = (this.y * invAlpha) + (target.y * alpha);
        return this;
    }

    public Vector2D lerp(Vector2D target, float alpha) {
        var invAlpha = 1.0f - alpha;
        this.x = (this.x * invAlpha) + (target.x * alpha);
        this.y = (this.y * invAlpha) + (target.y * alpha);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Vector2D) obj;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode() {
        var result = Double.hashCode(this.x);
        result = 31 * result + Double.hashCode(this.y);
        return result;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
