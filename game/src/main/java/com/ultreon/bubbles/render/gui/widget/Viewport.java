package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.util.helpers.Mth;

import java.awt.*;

public class Viewport extends Container {
    private final Rectangle viewportRect;
    private double yScroll;

    public Viewport(Rectangle viewportRect, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.viewportRect = viewportRect;
        setBackgroundColor(0xff333333);
    }

    @Override
    public void renderChildren(Renderer renderer) {
        Renderer viewportGraphics = renderer.subInstance(0, 0, viewportRect.width, viewportRect.height);
        viewportGraphics.translate(0, -yScroll);
        for (GuiComponent child : children) {
            child.render(viewportGraphics);
            viewportGraphics.dispose();
        }
    }

    @Override
    public void render(Renderer renderer) {
        this.renderComponent(renderer);

//        Renderer viewportGraphics = renderer.subInstance(0, 0, width, height);
        renderChildren(renderer);
    }

    @Override
    public void renderComponent(Renderer renderer) {
        renderer.color(getBackgroundColor());
        renderer.rect(0, 0, getSize().x, getSize().y);
    }

    @Override
    public void tick() {

    }

    public void setViewportSize(Dimension size) {
        this.viewportRect.setSize(size.width, size.height);
    }

    public Dimension getViewportSize() {
        return viewportRect.getSize().toAwtDimension();
    }

    public void setViewportLocation(Point location) {
        this.viewportRect.setPos(location.x, location.y);
    }

    public Point getViewportLocation() {
        return viewportRect.getPos().toAwtPoint();
    }

    public void setXScroll(double xScroll) {
        int width = getBounds().width;
        int viewportWidth = viewportRect.width;
        if (viewportWidth > width) {
            viewportRect.x = Mth.clamp((int) (viewportWidth * xScroll / width), 0, viewportWidth - width);
        }
    }

    public void setYScroll(double yScroll) {
        int height = getBounds().height;
        int viewportHeight = viewportRect.height;
        if (viewportHeight > height) {
            viewportRect.y = Mth.clamp((int) yScroll * viewportHeight, 0, viewportHeight);
            this.yScroll = Mth.clamp(yScroll * (viewportHeight - height), 0, viewportHeight - height);
        }
    }

    public double getXScroll() {
        int width = getBounds().width;
        int viewportWidth = viewportRect.width;
        if (viewportWidth > width) {
            return ((double) viewportRect.x / (double) viewportWidth * width) / width;
        } else {
            return 0;
        }
    }

    public double getYScroll() {
        int height = getBounds().height;
        int viewportHeight = viewportRect.height;
        if (viewportHeight > height) {
            return yScroll / (viewportHeight - height);
        } else {
            return 0;
        }
    }
}
