package com.ultreon.bubbles.util.position;

import com.ultreon.bubbles.util.position.util.RelativeAnchor;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Objects;

public class AWTWidgetRelativePosition extends RelativePosition {
    private Component component;
    private double relX;
    private double relY;
    private RelativeAnchor anchor;

    /**
     * Create relative point
     * Creates a point relative to the component, based on the anchor that is given by a parameter.
     *
     * @param component the {@link Component AWT component} to create relative point to.
     * @param relX      the relative X.
     * @param relY      the relatuve Y.
     * @param anchor    the anchor where to be relative from.
     * @return the relative {@link Point2D point} to the AWT Component.
     */
    private static Point2D getPoint(Component component, double relX, double relY, RelativeAnchor anchor) {
        double anchorY;
        double anchorX;
        if (anchor == RelativeAnchor.CENTER) {
            anchorX = component.getBounds().getCenterX();
            anchorY = component.getBounds().getCenterY();
        } else if (anchor == RelativeAnchor.TOP) {
            anchorX = component.getBounds().getCenterX();
            anchorY = component.getBounds().getMinY();
        } else if (anchor == RelativeAnchor.BOTTOM) {
            anchorX = component.getBounds().getCenterX();
            anchorY = component.getBounds().getMaxY();
        } else if (anchor == RelativeAnchor.LEFT) {
            anchorX = component.getBounds().getMinX();
            anchorY = component.getBounds().getCenterY();
        } else if (anchor == RelativeAnchor.RIGHT) {
            anchorX = component.getBounds().getMaxX();
            anchorY = component.getBounds().getCenterY();
        } else {
            return null;
        }

        return new Point2D.Double(anchorX + relX, anchorY + relY);
    }

    /**
     * AWT Widget Relative Position
     * A class to calculate the relative position from a AWT Widget.
     *
     * @param component the component to create a relative position from.
     * @param relX      the relative X.
     * @param relY      the relatuve Y.
     * @param anchor    the anchor where to be relative from.
     * @throws NullPointerException when the {@code anchor} parameter is invalid.
     * @see Point
     * @see Position
     */
    public AWTWidgetRelativePosition(Component component, int relX, int relY, RelativeAnchor anchor) {
        super(Objects.requireNonNull(AWTWidgetRelativePosition.getPoint(component, relX, relY, anchor)));

        this.component = component;
        this.relX = relX;
        this.relY = relY;
        this.anchor = anchor;
    }

    /**
     * Update
     * Updates the data, so it will be relative when changed data.
     *
     * @throws NullPointerException when the anchor parameter is invalid.
     */
    @Override
    public void tick() {
        Point2D point = getPoint(component, relX, relY, anchor);
        this.setPointX(Objects.requireNonNull(point).getX());
        this.setPointY(Objects.requireNonNull(point).getY());
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public double getRelX() {
        return relX;
    }

    @Override
    public double getRelY() {
        return relY;
    }

    public RelativeAnchor getAnchor() {
        return anchor;
    }

    public void setComponent(Component component) {
        this.component = component;
        tick();
    }

    @Override
    public void setRelX(double x) {
        this.relX = x;
        tick();
    }

    @Override
    public void setRelY(double y) {
        this.relY = y;
        tick();
    }

    public void setAnchor(RelativeAnchor anchor) {
        this.anchor = anchor;
        tick();
    }
}
