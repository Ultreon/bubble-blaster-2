package com.ultreon.bubbles.render.gui;

import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

public abstract class QComponent {
    private int x;
    private int y;
    private int width;
    private int height;

    protected Color backgroundColor;
    private Renderer renderer;

    public abstract void render(Renderer renderer);

    public abstract void renderComponent(Renderer renderer);

    @SuppressWarnings("EmptyMethod")
    public abstract void tick();

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setBounds(Rectangle bounds) {
        this.x = bounds.x;
        this.y = bounds.y;
        this.width = bounds.width;
        this.height = bounds.height;
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    public void setSize(Dimension size) {
        this.width = size.width;
        this.height = size.height;
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public void setLocation(Point location) {
        this.x = location.x;
        this.y = location.y;
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
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
