package com.ultreon.bubbles.entity.bubble;

import com.ultreon.commons.util.ColorUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BubbleSpec {
    private final List<BubbleCircle> circles;

    private BubbleSpec(List<BubbleCircle> circles) {
        this.circles = circles;
    }

    public BubbleSpec(BubbleSpec spec) {
        this.circles = new ArrayList<>(spec.circles);
    }

    public List<BubbleCircle> getCircles() {
        return circles;
    }

    public static class Builder {
        private final List<BubbleCircle> circles;
        private int index = 0;

        public Builder() {
            circles = new ArrayList<>();
        }

        public void add(Color color) {
            circles.add(new BubbleCircle(index, color));
            index++;
        }

        public void add(String hex) {
            circles.add(new BubbleCircle(index, ColorUtils.unpackHex(hex)));
            index++;
        }

        public BubbleSpec build() {
            return new BubbleSpec(circles);
        }
    }
}
