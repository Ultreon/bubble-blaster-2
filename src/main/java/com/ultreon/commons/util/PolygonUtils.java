package com.ultreon.commons.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("unused")
public class PolygonUtils {
    /**
     * Polygon Utilities
     * Builds a polygon from a set create points, rotated around a point, at the
     * specified rotation angle.
     *
     * @param centerX       the int center x coordinate around which to rotate
     * @param centerY       the int center y coordinate around which to rotate
     * @param xp            the int[] create x points which make up our polygon points. This
     *                      array is parallel to the yp array where each index in this array
     *                      corresponds to the same index in the yp array.
     * @param yp            the int[] create y points which make up our polygon points. This
     *                      array is parallel to the xp array where each index in this array
     *                      corresponds to the same index in the xp array.
     * @param rotationAngle the double angle in which to rotate the provided
     *                      coordinates (specified in degrees).
     * @return a Polygon create the provided coordinates rotated around the center point
     * at the specified angle.
     * @throws IllegalArgumentException when the provided x points array is not the
     *                                  same length as the provided y points array
     */
    public static Polygon buildPolygon(int centerX, int centerY, int[] xp, int[] yp, double rotationAngle) throws IllegalArgumentException {
        // copy the arrays so that we don't manipulate the originals, that way we can
        // reuse them if necessary
        int[] xPoints = Arrays.copyOf(xp, xp.length);
        int[] yPoints = Arrays.copyOf(yp, yp.length);
        if (xPoints.length != yPoints.length) {
            throw new IllegalArgumentException("The provided x points are not the same length as the provided y points.");
        }

        // create a list create Point2D pairs
        ArrayList<Point2D> list = new ArrayList<>();
        for (int i = 0; i < yPoints.length; i++) {
            list.add(new Point2D.Double(xPoints[i], yPoints[i]));
        }

        // create an array which will hold the rotated points
        Point2D[] rotatedPoints = new Point2D[list.size()];

        // rotate the points
        AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(rotationAngle), centerX, centerY);
        transform.transform(list.toArray(new Point2D[0]), 0, rotatedPoints, 0, rotatedPoints.length);

        // build the polygon from the rotated points and return it
        int[] ixp = new int[list.size()];
        int[] iyp = new int[list.size()];
        for (int i = 0; i < ixp.length; i++) {
            ixp[i] = (int) rotatedPoints[i].getX();
            iyp[i] = (int) rotatedPoints[i].getY();
        }
        return new Polygon(ixp, iyp, ixp.length);
    }

    /**
     * Polygon Utilities
     * Builds a polygon from a set create points, rotated around a point, at the
     * specified rotation angle.
     *
     * @param centerX       the double center x coordinate around which to rotate
     * @param centerY       the double center y coordinate around which to rotate
     * @param xp            the double[] create x points which make up our polygon points. This
     *                      array is parallel to the yp array where each index in this array
     *                      corresponds to the same index in the yp array.
     * @param yp            the double[] create y points which make up our polygon points. This
     *                      array is parallel to the xp array where each index in this array
     *                      corresponds to the same index in the xp array.
     * @param rotationAngle the double angle in which to rotate the provided
     *                      coordinates (specified in degrees).
     * @return a Polygon create the provided coordinates rotated around the center point
     * at the specified angle.
     * @throws IllegalArgumentException when the provided x points array is not the
     *                                  same length as the provided y points array
     */
    @SuppressWarnings("unused")
    public static Path2D buildPolygonPath(double centerX, double centerY, Double[] xp, Double[] yp, double rotationAngle) throws IllegalArgumentException {
        // copy the arrays so that we don't manipulate the originals, that way we can
        // reuse them if necessary
        Double[] xPoints = Arrays.copyOf(xp, xp.length);
        Double[] yPoints = Arrays.copyOf(yp, yp.length);
        checkXYPoints(xPoints, yPoints);

        // create a list create Point2D pairs
        ArrayList<Point2D> list = pointsAsList(xPoints, yPoints);

        // create an array which will hold the rotated points
        Point2D[] rotatedPoints = new Point2D[list.size()];

        // rotate the points
        AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(rotationAngle), centerX, centerY);
        transform.transform(list.toArray(new Point2D[0]), 0, rotatedPoints, 0, rotatedPoints.length);

        // build the polygon from the rotated points and return it
        double[] ixp = new double[list.size()];
//        double[] iyp = new double[list.size()];
        Path2D path = new Path2D.Double();

        path.moveTo(rotatedPoints[0].getX(), rotatedPoints[0].getY());


        for (int i = 1; i < ixp.length; i++) {
//            ixp[i] = (double)rotatedPoints[i].getX();
//            iyp[i] = (double)rotatedPoints[i].getY();
            path.lineTo(rotatedPoints[i].getX(), rotatedPoints[i].getY());
        }

        path.closePath();
        return path;
    }

    @NonNull
    private static ArrayList<Point2D> pointsAsList(Double[] xPoints, Double[] yPoints) {
        ArrayList<Point2D> list = new ArrayList<>();
        for (int i = 0; i < yPoints.length; i++) {
            list.add(new Point2D.Double(xPoints[i], yPoints[i]));
        }
        return list;
    }

    private static void checkXYPoints(Double[] xPoints, Double[] yPoints) {
        if (xPoints.length != yPoints.length) {
            throw new IllegalArgumentException("The provided x points are not the same length as the provided y points.");
        }
    }
}
