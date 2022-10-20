package com.ultreon.bubbles.render.shapes;

public interface Shape {
    boolean doIntersect(Shape shape) throws UnsupportedOperationException;
}