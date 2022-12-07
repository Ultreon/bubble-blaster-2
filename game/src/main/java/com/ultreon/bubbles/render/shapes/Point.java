package com.ultreon.bubbles.render.shapes;

public class Point {

    public Point(double X, double Y) {
        this.X = X;
        this.Y = Y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "X=" + X +
                ", Y=" + Y +
                '}';
    }

    public double getPointX() {
        return X;
    }

    public void setPointX(double pointX) {
        this.X = pointX;
    }

    public double getPointY() {
        return Y;
    }

    public void setPointY(double pointY) {
        this.Y = pointY;
    }

    private double X;
    private double Y;

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(X);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point) obj;
        if (Double.doubleToLongBits(X) != Double.doubleToLongBits(other.X)) {
            return false;
        }
        return Double.doubleToLongBits(Y) == Double.doubleToLongBits(other.Y);
    }


}