package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.util.helpers.Mth;

import java.awt.*;

public class Viewport extends Container {
    private final Rectangle viewportRect;
    protected double xScroll;
    protected double yScroll;

    public Viewport(Rectangle viewportRect, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.viewportRect = viewportRect;
        setBackgroundColor(0xff333333);
    }

    @Override
    public void renderChildren(Renderer renderer) {
        renderer.subInstance(0, 0, viewportRect.width, viewportRect.height, viewportRender -> {
            viewportRender.translate(-xScroll, -yScroll);
            for (GuiComponent child : children) {
                viewportRender.subInstance(child.getX(), child.getY(), child.getWidth(), child.getHeight(), child::render);
                viewportRender.dispose();
            }
        });
    }

    @Override
    public void render(Renderer renderer) {
        this.innerYOffset = (int) yScroll;
        this.innerXOffset = (int) xScroll;
        this.renderComponent(renderer);

//        Renderer viewportGraphics = renderer.subInstance(0, 0, width, height);
        renderChildren(renderer);
    }

    @Override
    public void renderComponent(Renderer renderer) {
        renderer.setColor(getBackgroundColor());
        renderer.rect(0, 0, getSize().x, getSize().y);
    }

    @Override
    public void tick() {

    }

    public void setViewportSize(Dimension size) {
        this.viewportRect.setSize(size.width, size.height);
    }

    public void setViewportSize(int width, int height) {
        this.viewportRect.setSize(width, height);
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
            viewportRect.x = (int) (this.xScroll = Mth.clamp(xScroll, 0, viewportWidth - height));
        }
    }

    public void setYScroll(double yScroll) {
        int height = getBounds().height;
        int viewportHeight = viewportRect.height;
        if (viewportHeight > height) {
            this.viewportRect.y = (int) (this.yScroll = Mth.clamp(yScroll, 0, viewportHeight - height));
        }
    }

    public double getXScroll() {
        int width = getBounds().width;
        int viewportWidth = viewportRect.width;
        if (viewportWidth > width) {
            return xScroll;
        } else {
            return 0;
        }
    }

    public double getYScroll() {
        int height = getBounds().height;
        int viewportHeight = viewportRect.height;
        if (viewportHeight > height) {
            return yScroll;
        } else {
            return 0;
        }
    }

    public double getXPercent() {
        return xScroll / (viewportRect.width - width);
    }

    public double getYPercent() {
        return yScroll / (viewportRect.height - height);
    }

    public void setXPercent(double percent) {
        this.xScroll = Mth.clamp(percent * (viewportRect.width - width), 0, viewportRect.width - width);
    }

    public void setYPercent(double percent) {
        this.yScroll = Mth.clamp(percent * (viewportRect.height - height), 0, viewportRect.height - height);
    }
}
