package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.libs.commons.v0.Mth;
import com.ultreon.libs.commons.v0.size.IntSize;


public class Viewport extends Container {
    private final Rectangle viewportRect;
    protected float xScroll;
    protected float yScroll;

    public Viewport(Rectangle viewportRect, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.viewportRect = viewportRect;
        this.setBackgroundColor(0xff333333);
    }

    @Override
    public void renderChildren(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        renderer.scissored(this.x, this.y, (int) this.viewportRect.width, (int) this.viewportRect.height, () -> {
            for (GuiComponent child : this.children) {
                renderer.scissored(child.getX(), child.getY(), child.getWidth(), child.getHeight(), () -> child.render(renderer, mouseX, mouseY, deltaTime));
            }
        });
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.innerYOffset = (int) this.yScroll;
        this.innerXOffset = (int) this.xScroll;
        this.renderComponent(renderer);

        this.renderChildren(renderer, mouseX, mouseY, deltaTime);
    }

    @Override
    public void renderComponent(Renderer renderer) {
        renderer.fill(this.x, this.y, this.getSize().x, this.getSize().y, this.backgroundColor);
    }

    public void setViewportSize(IntSize size) {
        this.viewportRect.setSize(size.width(), size.height());
    }

    public void setViewportSize(int width, int height) {
        this.viewportRect.setSize(width, height);
    }

    public Vector2 getViewportSize() {
        return this.viewportRect.getSize(new Vector2());
    }

    public void setViewportLocation(Vector2 location) {
        this.viewportRect.setPosition(location.x, location.y);
    }

    public Vector2 getViewportLocation() {
        return this.viewportRect.getPosition(new Vector2());
    }

    public void setXScroll(float xScroll) {
        float width = this.getBounds().width;
        float viewportWidth = this.viewportRect.width;
        if (viewportWidth > width) {
            this.viewportRect.x = (int) (this.xScroll = Mth.clamp(xScroll, 0, viewportWidth - this.height));
        }
    }

    public void setYScroll(float yScroll) {
        float height = this.getBounds().height;
        float viewportHeight = this.viewportRect.height;
        if (viewportHeight > height) {
            this.viewportRect.y = (int) (this.yScroll = Mth.clamp(yScroll, 0, viewportHeight - height));
        }
    }

    public float getXScroll() {
        float width = this.getBounds().width;
        float viewportWidth = this.viewportRect.width;
        if (viewportWidth > width) {
            return this.xScroll;
        } else {
            return 0;
        }
    }

    public float getYScroll() {
        float height = this.getBounds().height;
        float viewportHeight = this.viewportRect.height;
        if (viewportHeight > height) {
            return this.yScroll;
        } else {
            return 0;
        }
    }

    public double getXPercent() {
        return this.xScroll / (this.viewportRect.width - this.width);
    }

    public double getYPercent() {
        return this.yScroll / (this.viewportRect.height - this.height);
    }

    public void setXPercent(float percent) {
        this.xScroll = Mth.clamp(percent * (this.viewportRect.width - this.width), 0, this.viewportRect.width - this.width);
    }

    public void setYPercent(float percent) {
        this.yScroll = Mth.clamp(percent * (this.viewportRect.height - this.height), 0, this.viewportRect.height - this.height);
    }
}
