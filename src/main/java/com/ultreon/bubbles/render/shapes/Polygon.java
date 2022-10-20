package com.ultreon.bubbles.render.shapes;

import com.ultreon.bubbles.util.CollisionUtils;
import com.ultreon.commons.util.Constants;

import java.util.ArrayList;

public class Polygon implements Shape {
    private Point[] points;

    public Polygon(Point[] points) {
        this.points = points;
    }

    public Polygon(java.awt.Polygon polygon) {
        int[] xps = polygon.xpoints;
        int[] yps = polygon.ypoints;
        int np = polygon.npoints;

        ArrayList<Point> points1 = new ArrayList<>();
        Point[] points2 = new Point[]{};

        for (int i = 0; i < np; i++) {
            int xp = xps[i];
            int yp = yps[i];
            points1.add(new Point(xp, yp));
        }

        points1.toArray(points2);
        this.points = points2;
    }

    /**
     * @return the points
     */
    public Point[] getPoints() {
        return points;
    }

    /**
     * @param points the points to set
     */
    public void setPoints(Point[] points) {
        this.points = points;
    }

    @Override
    public boolean doIntersect(Shape shape) {
        if (shape instanceof Circle)
            return CollisionUtils.doIntersect((Circle) shape, this);
        else if (shape instanceof Line)
            return CollisionUtils.doIntersect(this, (Line) shape);
        else
            throw new UnsupportedOperationException(Constants.UNSUPPORTED_SHAPE);
    }
}