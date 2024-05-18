package dev.ultreon.bubbles.entity.bubble;

import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.util.ColorUtils;

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
        return this.circles;
    }

    public static class Builder {
        private final List<BubbleCircle> circles;
        private int index = 0;

        public Builder() {
            this.circles = new ArrayList<>();
        }

        public void add(Color color) {
            this.circles.add(new BubbleCircle(this.index, color));
            this.index++;
        }

        public void add(String hex) {
            this.circles.add(new BubbleCircle(this.index, ColorUtils.unpackHex(hex)));
            this.index++;
        }

        public BubbleSpec build() {
            return new BubbleSpec(this.circles);
        }
    }
}
