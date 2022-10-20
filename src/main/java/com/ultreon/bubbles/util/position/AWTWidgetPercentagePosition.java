package com.ultreon.bubbles.util.position;

import com.ultreon.bubbles.util.position.util.RelativeAnchor;

import java.awt.*;
import java.awt.geom.Point2D;

public class AWTWidgetPercentagePosition extends PercentagePosition {
    private Component component;
    private RelativeAnchor anchor;

    /**
     * AWT Widget Relative Position
     * A class to calculate the relative position from a AWT Widget.
     *
     * @param component the component to create a relative position from.
     * @param percentX  the relative X in percentages (0.0d - 1.0d == 0% - 100%).
     * @param percentY  the relative Y in percentages (0.0d - 1.0d == 0% - 100%).
     * @throws NullPointerException when the {@code anchor} parameter is invalid.
     * @see Point
     * @see Position
     */
    public AWTWidgetPercentagePosition(Component component, int percentX, int percentY) {
        super(new Point2D.Double(component.getWidth(), component.getHeight()), percentX, percentY);
        this.component = component;
    }

    /**
     * Update
     * Updates the data, so it will be relative when changed data.
     *
     * @throws NullPointerException when the anchor parameter is invalid.
     */
    @Override
    public void tick() {
        setPointX(component.getX() * getPercentX());
        setPointY(component.getY() * getPercentY());
    }

    public Component getComponent() {
        return component;
    }

    public RelativeAnchor getAnchor() {
        return anchor;
    }

    public void setComponent(Component component) {
        this.component = component;
        tick();
    }

    public void setAnchor(RelativeAnchor anchor) {
        this.anchor = anchor;
        tick();
    }
}
