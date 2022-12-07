package com.ultreon.bubbles.render.gui;

import com.ultreon.commons.util.PolygonUtils;

import java.awt.*;

public class Background {
    @SuppressWarnings({"ConstantConditions", "PointlessArithmeticExpression"})
    public static Shape getShape(int x, int y, int width, int height) {
        int x1 = 0 * width / 10, y1 = -25 * height / 50;
        int x2 = 5 * width / 10, y2 = -25 * height / 50;
        int x3 = 0 * width / 10, y3 = 25 * height / 50;
        int x4 = -5 * width / 10, y4 = 25 * height / 50;
        return PolygonUtils.buildPolygon(x, y, new int[]{x1, x2, x3, x4}, new int[]{y1, y2, y3, y4}, 0.0);
    }
}
