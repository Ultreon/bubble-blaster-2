package com.ultreon.bubbles.common;

import com.ultreon.bubbles.vector.Vec2i;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.Contract;

import java.awt.*;

public class PolygonBuilder {
    private final IntList pointsX = new IntArrayList();
    private final IntList pointsY = new IntArrayList();
    private int length = 0;

    public PolygonBuilder() {

    }

    @Contract(value = "_ -> this", pure = true)
    public PolygonBuilder add(Point point) {
        return this.add(point.x, point.y);
    }

    @Contract(value = "_ -> this", pure = true)
    public PolygonBuilder add(Vec2i point) {
        return this.add(point.x, point.y);
    }

    @Contract(value = "_, _ -> this", pure = true)
    public PolygonBuilder add(int x, int y) {
        pointsX.add(x);
        pointsY.add(y);
        length++;

        return this;
    }


    @Contract(value = "-> new", pure = true)
    public Polygon build() {
        return new Polygon(pointsX.toIntArray(), pointsY.toIntArray(), length);
    }
}
