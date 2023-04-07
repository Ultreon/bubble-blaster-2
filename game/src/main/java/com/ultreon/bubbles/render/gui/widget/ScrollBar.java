package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.bubbles.vector.Vec2i;
import org.checkerframework.common.value.qual.IntRange;

public class ScrollBar extends GuiComponent {
    public static final int SIZE = 20;

    private double percent = 0.0;
    private double scale;
    private ScrollHandler scrollHandler;
    private boolean dragging;
    private int draggingFrom;

    /**
     * @param x      position create the widget
     * @param y      position create the widget
     * @param width  size create the widget
     * @param height size create the widget
     */
    public ScrollBar(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Renderer renderer) {
        setWidth(SIZE);
        renderer.color(0x40000000);
        renderer.fill(getBounds());
        renderer.color(0xff555555);
        renderer.fill(getThumbBounds());
    }

    private Rectangle getThumbBounds() {
        return new Rectangle(getX() + getWidth() / 2 - 2, (int) (percent * (getHeight() - scale())) + (getWidth() / 2 - 2), 3, (int) (scale()) - (getWidth() / 2 - 2) * 2);
    }

    private double scale() {
        return Mth.clamp(getHeight() * scale, SIZE * 2, getHeight());
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = Mth.clamp(percent, 0.0, 1.0);
    }

    @Override
    public void renderComponent(Renderer renderer) {

    }

    @Override
    public void tick() {

    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setOnScroll(ScrollHandler scrollHandler) {
        this.scrollHandler = scrollHandler;
    }

    @Override
    public void mouseDrag(int x, int y, int nx, int ny, int button) {
        if (dragging) {
            float percent = (float)ny / this.getHeight();
            this.setPercent(percent);
            this.scrollHandler.onScroll(percent);
        }
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (this.scrollHandler != null) {
            if (!this.getThumbBounds().contains(x, y)) {
                float percent = (float) y / this.getHeight();
                this.setPercent(percent);
                this.scrollHandler.onScroll(percent);
            } else {
                this.dragging = true;
                this.draggingFrom = y;
            }
        }
        return true;
    }

    @FunctionalInterface
    public interface ScrollHandler {
        void onScroll(double percent);
    }
}
