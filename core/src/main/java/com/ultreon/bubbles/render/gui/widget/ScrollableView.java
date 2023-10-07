package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.google.common.annotations.Beta;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Beta
public class ScrollableView extends Container {
    public static final int SCROLLBAR_WIDTH = ScrollBar.SIZE;
    @NotNull
    private final ScrollBar scrollBar;
    @NotNull
    private Viewport viewport;

    public ScrollableView(@NotNull Rectangle viewport, int x, int y, int width, int height) {
        this(new Viewport(viewport, x, y, width - SCROLLBAR_WIDTH, height), x, y, width, height);
    }

    public ScrollableView(@NotNull Viewport viewport, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.viewport = super.add(viewport);
        this.scrollBar = super.add(new ScrollBar(x + width - SCROLLBAR_WIDTH, y, SCROLLBAR_WIDTH, height));
        this.scrollBar.setOnScroll(percent -> this.viewport.setYPercent(percent));
    }

    @Override
    public void renderChildren(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.viewport.setWidth(this.getWidth() - SCROLLBAR_WIDTH);
        this.viewport.setHeight(this.getHeight());
        this.viewport.setX(this.x);
        this.viewport.setY(this.y);
        renderer.scissored(this.viewport.getBounds(), () -> this.viewport.render(renderer, mouseX, mouseY, deltaTime));
        this.scrollBar.setWidth(SCROLLBAR_WIDTH);
        this.scrollBar.setHeight(this.getHeight());
        this.scrollBar.setX(this.x + this.getWidth() - SCROLLBAR_WIDTH);
        this.scrollBar.setY(this.y);
        renderer.scissored(this.scrollBar.getBounds(), () -> this.scrollBar.render(renderer, mouseX, mouseY, deltaTime));
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.updateXYOffset();
        this.scrollBar.setPercent(this.viewport.getYPercent());
        this.scrollBar.setScale(1 - ((double) this.viewport.getViewportSize().y - (double) this.viewport.getHeight()) / (double) this.viewport.getViewportSize().y);

        this.renderComponent(renderer);
        this.renderChildren(renderer, mouseX, mouseY, deltaTime);
    }

    private void updateXYOffset() {
        this.innerXOffset = (int) -this.viewport.xScroll - this.x;
        this.innerYOffset = (int) -this.viewport.yScroll - this.y;
    }

    @Override
    public void renderComponent(Renderer renderer) {
        renderer.fill(this.x, this.y, this.width, this.height, this.getBackgroundColor());
    }

    @Override
    public @Nullable GuiComponent getWidgetAt(int x, int y) {
        this.updateXYOffset();
        return this.viewport.getWidgetAt(x, y);
    }

    public @NotNull Viewport getViewport() {
        return this.viewport;
    }

    public void setViewport(@NotNull Viewport viewport) {
        this.viewport = viewport;
    }

    public void setXScroll(float percent) {
        this.viewport.setXScroll(percent);
    }

    public void setYScroll(float percent) {
        this.viewport.setYScroll(percent);
    }

    @Override
    public <T extends GuiComponent> T add(T child) {
        this.viewport.add(child);
        return child;
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (this.scrollBar.isWithinBounds(x, y) && this.scrollBar.mousePress(x, y, button)) {
            return true;
        }
        return super.mousePress(x, y, button);
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        return super.mouseRelease(x, y, button);
    }

    @Override
    public boolean mouseClick(int x, int y, int button, int count) {
        if (this.scrollBar.isWithinBounds(x, y) && this.scrollBar.mouseClick(x, y, button, count)) {
            return true;
        }
        return super.mouseClick(x, y, button, count);
    }

    @Override
    public void mouseDrag(int x, int y, int nx, int ny, int button) {
        if (this.scrollBar.isWithinBounds(x, y)) {
            this.scrollBar.mouseDrag(x, y, nx, ny, button);
            return;
        }
        super.mouseDrag(x, y, nx, ny, button);
    }

    @Override
    public boolean keyPress(int keyCode) {
        return super.keyPress(keyCode);
    }

    @Override
    public boolean keyRelease(int keyCode) {
        return super.keyRelease(keyCode);
    }

    @Override
    public void remove(GuiComponent child) {
        this.viewport.remove(child);
    }

    @Override
    public boolean mouseWheel(int x, int y, float rotation) {
        x -= this.x + this.innerXOffset;
        y -= this.y + this.innerYOffset;
        if (this.viewport.isWithinBounds(x, y) && this.viewport.mouseWheel(x, y, rotation)) {
            return true;
        }
        this.viewport.setYScroll(this.viewport.getYScroll() + rotation * 300 / 10);
        return true;
    }
}
