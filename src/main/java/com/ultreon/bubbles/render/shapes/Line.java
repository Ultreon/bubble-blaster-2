package com.ultreon.bubbles.render.shapes;


import com.ultreon.bubbles.util.CollisionUtils;
import com.ultreon.commons.util.Constants;

public class Line implements Shape {
    private double slope;
    private double yintercept;
    private Point pointA;
    private Point pointB;

    /**
     * Constructs a Line object given 2 points
     * Point A and Point B
     */
    public Line(Point pointA, Point pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
        calculateAndSetSlope();
        calculateAndSetYIntercept();
    }

    public Line(Point point, double slope) {
        this.pointA = point;
        this.slope = slope;
    }

    public Line(double slope, double yintercept) {
        this.slope = slope;
        this.yintercept = yintercept;
    }

/*public double getSlope() {

    if(pointA != null && pointB!=null){
        double X1 = pointA.getPointX();
        double Y1 = pointA.getPointY();
        double X2 = pointB.getPointX();
        double Y2 = pointB.getPointY();

        return (Y2 - Y1)/(X2 - X1);
    } else if(pointA !=null && yintercept != 0.0){
        return 0.0;
    }
}*/

    public double getSlope() {
        return slope;
    }

    private void calculateAndSetSlope() {
        if (Math.abs((this.pointA.getPointY() - this.pointB.getPointY())) < Constants.EPSILON)
            this.slope = 0.0;
        else if (Math.abs((this.pointA.getPointX() - this.pointB.getPointX())) < Constants.EPSILON)
            this.slope = Double.POSITIVE_INFINITY;
        else
            this.slope = (pointA.getPointY() - pointB.getPointY()) / (pointA.getPointX() - pointB.getPointX());
    }


    public Point getPointA() {
        return pointA;
    }

    public void setPointA(Point pointA) {
        this.pointA = pointA;
    }

    public Point getPointB() {
        return pointB;
    }

    public void setPointB(Point pointB) {
        this.pointB = pointB;
    }

    /**
     * @return the yintercept
     */
    public double getYintercept() {
        return yintercept;
    }

    /**
     * @param yintercept the yintercept to set
     */
    public void setYintercept(double yintercept) {
        this.yintercept = yintercept;
    }

    private void calculateAndSetYIntercept() {
        if (this.slope == 0.0)
            yintercept = pointA.getPointY();
        else if (Double.isInfinite(slope))
            yintercept = Double.POSITIVE_INFINITY;
        else
            yintercept = pointA.getPointY() - (slope * pointA.getPointX());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pointA == null) ? 0 : pointA.hashCode());
        result = prime * result + ((pointB == null) ? 0 : pointB.hashCode());
        long temp;
        temp = Double.doubleToLongBits(slope);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yintercept);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Line other = (Line) obj;
        if (pointA == null) {
            if (other.pointA != null)
                return false;
        } else if (!pointA.equals(other.pointA))
            return false;
        if (pointB == null) {
            if (other.pointB != null)
                return false;
        } else if (!pointB.equals(other.pointB))
            return false;
        if (Double.doubleToLongBits(slope) != Double
                .doubleToLongBits(other.slope))
            return false;
        return Double.doubleToLongBits(yintercept) == Double
                .doubleToLongBits(other.yintercept);
    }

    @Override
    public boolean doIntersect(Shape shape) {
        if (shape instanceof Circle)
            return CollisionUtils.doIntersect(this, (Circle) shape);
        else if (shape instanceof Polygon)
            return CollisionUtils.doIntersect((Polygon) shape, this);
        else if (shape instanceof Line)
            return CollisionUtils.doIntersectLineSegments(this, (Line) shape);
        else
            throw new UnsupportedOperationException(Constants.UNSUPPORTED_SHAPE);
    }
}
