package com.ultreon.bubbles.render.shapes;

import com.ultreon.bubbles.util.CollisionUtils;
import com.ultreon.commons.util.Constants;

public class Circle implements Shape {

    private Point center;
    private double radius;

    @Override
    public String toString() {
        return "Circle{" +
                "center=" + center +
                ", radius=" + radius +
                '}';
    }

    public Circle(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    /**
     * @return the center
     */
    public Point getCenter() {
        return center;
    }

    public java.awt.Point getAwtCenter() {
        return new java.awt.Point((int) center.getPointX(), (int) center.getPointY());
    }

    /**
     * @param center the center to set
     */
    public void setCenter(Point center) {
        this.center = center;
    }

    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean doIntersect(Shape shape) {
        if (shape instanceof Polygon)
            return CollisionUtils.doIntersect(this, (Polygon) shape);
        else if (shape instanceof Line)
            return CollisionUtils.doIntersect((Line) shape, this);
        else
            throw new UnsupportedOperationException(Constants.UNSUPPORTED_SHAPE);
    }
}
