package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.gui.Shape;
import com.ultreon.bubbles.vector.Vec2i;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Rectangle extends Shape implements Externalizable, Cloneable {
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle() {
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("Width should be positive.");
        }
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (width < 0) {
            throw new IllegalArgumentException("Height should be positive.");
        }
        this.height = height;
    }

    public Vec2i getPos() {
        return new Vec2i(x, y);
    }

    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }

    public Vec2i getSize() {
        return new Vec2i(width, height);
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void setBounds(int x, int y, int width, int height) {
        setPos(x, y);
        setSize(width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean contains(int x, int y) {
        return x >= this.x && y >= this.y && x <= this.x + width && y <= this.y + height;
    }

    public boolean contains(Vec2i pos) {
        return pos.x >= this.x && pos.y >= this.y && pos.x <= this.x + width && pos.y <= this.y + height;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.width);
        out.writeInt(this.height);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readInt();
        this.y = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    @Override
    public Rectangle clone() {
        try {
            Rectangle clone = (Rectangle) super.clone();

            clone.x = this.x;
            clone.y = this.y;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
