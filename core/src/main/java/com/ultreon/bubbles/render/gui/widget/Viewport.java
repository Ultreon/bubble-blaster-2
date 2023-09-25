package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.libs.commons.v0.size.IntSize;


public class Viewport extends Container {
    private final Rectangle viewportRect;
    protected float xScroll;
    protected float yScroll;

    public Viewport(Rectangle viewportRect, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.viewportRect = viewportRect;
        setBackgroundColor(0xff333333);
    }

    @Override
    public void renderChildren(Renderer renderer) {
        renderer.subInstance(0, 0, (int) viewportRect.width, (int) viewportRect.height, viewportRender -> {
            viewportRender.translate(-xScroll, -yScroll);
            for (GuiComponent child : children) {
                viewportRender.subInstance(child.getX(), child.getY(), child.getWidth(), child.getHeight(), child::render);
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

    public void setViewportSize(IntSize size) {
        this.viewportRect.setSize(size.width(), size.height());
    }

    public void setViewportSize(int width, int height) {
        this.viewportRect.setSize(width, height);
    }

    public Vector2 getViewportSize() {
        return viewportRect.getSize(new Vector2());
    }

    public void setViewportLocation(Vector2 location) {
        this.viewportRect.setPosition(location.x, location.y);
    }

    public Vector2 getViewportLocation() {
        return viewportRect.getPosition(new Vector2());
    }

    public void setXScroll(float xScroll) {
        float width = getBounds().width;
        float viewportWidth = viewportRect.width;
        if (viewportWidth > width) {
            viewportRect.x = (int) (this.xScroll = Mth.clamp(xScroll, 0, viewportWidth - height));
        }
    }

    public void setYScroll(float yScroll) {
        float height = getBounds().height;
        float viewportHeight = viewportRect.height;
        if (viewportHeight > height) {
            this.viewportRect.y = (int) (this.yScroll = Mth.clamp(yScroll, 0, viewportHeight - height));
        }
    }

    public float getXScroll() {
        float width = getBounds().width;
        float viewportWidth = viewportRect.width;
        if (viewportWidth > width) {
            return xScroll;
        } else {
            return 0;
        }
    }

    public float getYScroll() {
        float height = getBounds().height;
        float viewportHeight = viewportRect.height;
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

    public void setXPercent(float percent) {
        this.xScroll = Mth.clamp(percent * (viewportRect.width - width), 0, viewportRect.width - width);
    }

    public void setYPercent(float percent) {
        this.yScroll = Mth.clamp(percent * (viewportRect.height - height), 0, viewportRect.height - height);
    }
}
