package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.gui.Shape;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class Circle extends Shape {
    private int x;
    private int y;
    private int radius;

    public Circle(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.x - (float) this.radius / 2, this.y - (float) this.radius / 2, this.radius, this.radius);
    }

    @Override
    public boolean contains(int x, int y) {
        int dx = abs(x - this.x);
        int dy = abs(y - this.y);
        int r = this.radius;

        if (dx > r) {
            return false;
        }
        if (dy > r) {
            return false;
        }
        if (dx + dy <= r) {
            return true;
        }

        return pow(dx, 2) + pow(dy, 2) <= pow(r, 2);
    }
}
