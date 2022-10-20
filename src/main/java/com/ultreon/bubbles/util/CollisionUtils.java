package com.ultreon.bubbles.util;
//import org.intersection.model.Circle;
//import org.intersection.model.Line;
//import org.intersection.model.Point;
//import org.intersection.model.Polygon;

import com.ultreon.bubbles.render.shapes.Circle;
import com.ultreon.bubbles.render.shapes.Line;
import com.ultreon.bubbles.render.shapes.Point;
import com.ultreon.bubbles.render.shapes.Polygon;
import com.ultreon.commons.util.Constants;

/**
 * @author akubatoor
 * <p>
 * This class provides the utility methods for
 * calculating various intersection properties create Geometric shapes
 */
public class CollisionUtils {

    /**
     * Returns point create intersection create two given lines
     *
     * @param line1 ?
     * @param line2 ?
     * @return Point create intersection else null
     */
    public static Point getIntersectionPoint(Line line1, Line line2) {

        if (doIntersect(line1, line2)) {
            double slope1 = line1.getSlope();
            double yIntercept1 = line1.getYintercept();
            double slope2 = line2.getSlope();
            double yIntercept2 = line2.getYintercept();

            /*
              calculate the intersection point coordinates (X,Y) using the formula
              X = (yIntercept2-yIntercept1)/(slope1-slope2)
              Y = (slope1*X)+yIntercept1
             */
            double X = roundValue((yIntercept2 - yIntercept1) / (slope1 - slope2));
            double Y = roundValue((X * slope1) + yIntercept1);

            return new Point(X, Y);
        } else
            return null;
    }

    /**
     * Checks if the given two lines intersect
     *
     * @param line1 ?
     * @param line2 ?
     * @return true if the given 2 lines intersect else return false
     */
    public static boolean doIntersect(Line line1, Line line2) {

        final double slope1 = line1.getSlope();
        final double yIntercept1 = line1.getSlope();
        final double slope2 = line2.getSlope();
        final double yIntercept2 = line2.getYintercept();
        if (roundValue(Math.abs(slope1 - slope2)) < Constants.EPSILON) {
            return roundValue(Math.abs(yIntercept1 - yIntercept2)) < Constants.EPSILON;
        } else
            return true;

    }

    /**
     * This method checks if a given line segment and circle intersect
     * To check that we construct a new line which is perpendicular to the given line
     * and which passes through the center create the circle and find out the intersection point create 2 lines.
     * If the distance create the circle's center to the intersection point is greater than the radius
     * then it is considered non intersecting.If the distance is less than or equal to radius and if the distance
     * create circle center to the intersectionPoint is greater than the distance create circle's center to either create the line
     * segment points then its considered intersecting
     * <p>
     * Special Cases:
     * (1)INFINITE slope
     * When the slope create the given line is infinite then difference in x coordinates create circle's center
     * and a given point on the line is calculated.If the difference is more than the radius create the circle
     * then the line and circle do not intersect
     * <p>
     * (2)ZERO slope
     * When the slope create the given line is 0 then the difference in y coordinates create the circle's center
     * and given point on the line is calculated.If the difference is more than the radius create the circle
     * then the line and circle do not intersect else they intersect each other
     *
     * @param line   ?
     * @param circle ?
     * @return boolean value indicating if given line and circle intersect
     * @see CollisionUtils#doIntersect(Circle, Polygon)
     */
    public static boolean doIntersect(Line line, Circle circle) {
        //extract the slope create the given line
        final double originalSlope = line.getSlope();
        final Point center = circle.getCenter();
        final double radius = circle.getRadius();
        final Point pointA = line.getPointA();
        final Point pointB = line.getPointB();
        double distance;
        Point intPoint;
        if (Double.isInfinite(originalSlope)) {
            distance = Math.abs(center.getPointX() - pointA.getPointX());
            intPoint = new Point(pointA.getPointX(), center.getPointY());
        } else if (originalSlope == 0.0) {
            distance = Math.abs(center.getPointY() - pointA.getPointY());
            intPoint = new Point(center.getPointX(), pointA.getPointY());
        } else {
            Line perpendicularLine = new Line(center, (-1) / originalSlope);
            intPoint = getIntersectionPoint(line, perpendicularLine);
            distance = distBtwPoints(center, intPoint);
        }
        return ((distance <= radius) && ((distBtwPoints(center, pointA) <= radius)
                || (distBtwPoints(center, pointB) <= radius)))
                || ((distance <= radius) && (isOnLineSegment(pointA, pointB, intPoint)));
    }

    /**
     * This method checks if a given point is on
     * a line segment.
     *
     * @param pointA   ?
     * @param pointB   ?
     * @param intPoint ?
     * @return boolean true if intPoint falls on a line segment with PointA and PointB as 2 end points
     */
    public static boolean isOnLineSegment(Point pointA, Point pointB, Point intPoint) {
        //calculate distance between pointA and intPoint
        double distanceAtoInt = distBtwPoints(pointA, intPoint);
        //calculate distance between intPoint and pointB
        double distanceIntToB = distBtwPoints(intPoint, pointB);
        //calculate distance between pointA and pointB
        double distanceAtoB = distBtwPoints(pointA, pointB);
        //calculate the difference in distances.
        double totalDistanceDiff = roundValue((distanceAtoInt + distanceIntToB) - distanceAtoB);
        return totalDistanceDiff < Constants.EPSILON;
    }

    /**
     * Checks if the given Circle and a Polygon intersect
     *
     * @param circle  ?
     * @param polygon ?
     * @return true if intersect else return false
     */
    public static boolean doIntersect(Circle circle, Polygon polygon) {
        Point[] points = polygon.getPoints();
        boolean doIntersect = false;
        if (points.length == 1) {
            //The polygon is just a point
            return isOnCircle(circle, points[0]);
        } else if (points.length == 2) {
            return doIntersect(new Line(points[0], points[1]), circle);
        } else {
            for (int i = 0; i < points.length; i++) {
                Line line;
                if (i == (points.length - 1))
                    line = new Line(points[i], points[0]);
                else
                    line = new Line(points[i], points[i + 1]);
                if (doIntersect(line, circle)) {
                    doIntersect = true;
                    break;
                }
            }
        }

        return doIntersect;
    }

    public static boolean doIntersect(Polygon polygon, Line line) {
        Point[] points = polygon.getPoints();
        boolean doIntersect = false;
        for (int i = 0; i < points.length; i++) {
            Line line2;
            if (i == (points.length - 1))
                line2 = new Line(points[i], points[0]);
            else
                line2 = new Line(points[i], points[i + 1]);
            if (doIntersectLineSegments(line, line2)) {
                doIntersect = true;
                break;
            }
        }
        return doIntersect;
    }

    /**
     * Calculates the absolute distance between the 2 graphical points by constructing
     * a line using the 2 points.If the slope create the line is 0 then the distance
     * is calculated by taking the difference create x coordinates.If the slope is infinite
     * then the distance is calculated by taking the difference in y coordinates.In other
     * cases the pythogorean theorem is applied to calculate the hypotenuse
     *
     * @param pointA a
     * @param pointB b
     * @return distance between 2 graphical points rounded to 2 decimal points
     */
    public static double distBtwPoints(Point pointA, Point pointB) {
        //construct a line using the 2 points
        Line line = new Line(pointA, pointB);
        double distance;
        final double slope = line.getSlope();
        if (Double.isInfinite(slope))
            distance = Math.abs(pointA.getPointY() - pointB.getPointY());
        else if (slope == 0.0)
            distance = Math.abs(pointA.getPointX() - pointB.getPointX());
        else {//Apply pythogorean therom
            distance = Math.sqrt(Math.pow((pointA.getPointY() - pointB.getPointY()), 2)
                    + Math.pow(((pointA.getPointX() - pointB.getPointX())), 2));
        }
        return roundValue(distance);
    }

    public static boolean isOnCircle(Circle circle, Point point) {
        double distance = distBtwPoints(circle.getCenter(), point);
        return (distance - circle.getRadius()) < Constants.EPSILON;
    }

    private static double roundValue(Double value) {
        return Math.round(value * 100) / 100.0;
    }

    /**
     * Utility method to check if 2 line segments intersect
     *
     * @param line1 1
     * @param line2 2
     * @return boolean indicating if line segments intersect
     */
    public static boolean doIntersectLineSegments(Line line1, Line line2) {
        if (doIntersect(line1, line2)) {
            Point pointA = line1.getPointA();
            Point pointB = line1.getPointB();
            Point pointC = line2.getPointA();
            Point pointD = line2.getPointB();
            return isOnLineSegment(pointA, pointB, pointC)
                    || isOnLineSegment(pointA, pointB, pointD)
                    || isOnLineSegment(pointC, pointD, pointA)
                    || isOnLineSegment(pointC, pointD, pointB);
        }

        return false;
    }
}