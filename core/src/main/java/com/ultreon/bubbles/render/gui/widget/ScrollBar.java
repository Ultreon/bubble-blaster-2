package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.libs.commons.v0.Mth;
import org.checkerframework.common.value.qual.IntRange;

public class ScrollBar extends GuiComponent {
    public static final int SIZE = 20;

    private double percent = 0.0;
    private double scale;
    private ScrollHandler scrollHandler;
    private boolean dragging;

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
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.setWidth(SIZE);

        Rectangle thumbBounds = this.getThumbBounds();
        renderer.fill(this.x, this.y, this.width, this.height, Color.BLACK.withAlpha(0x40));
        renderer.fill((int) thumbBounds.x, (int) thumbBounds.y, (int) thumbBounds.width, (int) thumbBounds.height, Color.WHITE.withAlpha(0x60));
    }

    private Rectangle getThumbBounds() {
        return new Rectangle(this.x + this.getWidth() / 2f - 2, this.y + (float) (this.percent * (this.getHeight() - this.scale())) + (this.getWidth() / 2f - 2), 3, (float) (this.scale()) - (this.getWidth() / 2f - 2) * 2);
    }

    private double scale() {
        return Mth.clamp(this.getHeight() * this.scale, SIZE * 2, this.getHeight());
    }

    public double getPercent() {
        return this.percent;
    }

    public void setPercent(double percent) {
        this.percent = Mth.clamp(percent, 0.0, 1.0);
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setOnScroll(ScrollHandler scrollHandler) {
        this.scrollHandler = scrollHandler;
    }

    @Override
    public void mouseDrag(int x, int y, int nx, int ny, int button) {
        if (this.dragging) {
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
            }
        }
        return true;
    }

    @FunctionalInterface
    public interface ScrollHandler {
        void onScroll(float percent);
    }
}
