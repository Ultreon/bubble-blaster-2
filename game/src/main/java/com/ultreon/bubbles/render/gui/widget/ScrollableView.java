package com.ultreon.bubbles.render.gui.widget;

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
    public void renderChildren(Renderer renderer) {
        this.viewport.setWidth(getWidth() - SCROLLBAR_WIDTH);
        this.viewport.setHeight(getHeight());
        this.viewport.setX(0);
        this.viewport.setY(0);
        this.viewport.render(renderer.subInstance(viewport.getBounds()));
        this.scrollBar.setWidth(SCROLLBAR_WIDTH);
        this.scrollBar.setHeight(getHeight());
        this.scrollBar.setX(getWidth() - SCROLLBAR_WIDTH);
        this.scrollBar.setY(0);
        this.scrollBar.render(renderer.subInstance(scrollBar.getBounds()));
    }

    @Override
    public void render(Renderer renderer) {
        this.updateXYOffset();
        this.scrollBar.setPercent(this.viewport.getYPercent());
        this.scrollBar.setScale(1 - (((double) this.viewport.getViewportSize().height - (double) this.viewport.getHeight()) / (double) this.viewport.getViewportSize().height));

        renderComponent(renderer);
        renderChildren(renderer);
    }

    private void updateXYOffset() {
        this.innerXOffset = (int) -this.viewport.xScroll;
        this.innerYOffset = (int) -this.viewport.yScroll;
    }

    @Override
    public void renderComponent(Renderer renderer) {
        fill(renderer, 0, 0, width, height, getBackgroundColor());
    }

    @Override
    public void tick() {

    }

    @Override
    public @Nullable GuiComponent getWidgetAt(int x, int y) {
        this.updateXYOffset();
        return viewport.getWidgetAt(x, y);
    }

    public @NotNull Viewport getViewport() {
        return viewport;
    }

    public void setViewport(@NotNull Viewport viewport) {
        this.viewport = viewport;
    }

    public void setXScroll(double percent) {
        this.viewport.setXScroll(percent);
    }

    public void setYScroll(double percent) {
        this.viewport.setYScroll(percent);
    }

    @Override
    public <T extends GuiComponent> T add(T child) {
        this.viewport.add(child);
        return child;
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (scrollBar.isWithinBounds(x, y) && scrollBar.mousePress(x, y, button)) {
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
        if (scrollBar.isWithinBounds(x, y) && scrollBar.mouseClick(x, y, button, count)) {
            return true;
        }
        return super.mouseClick(x, y, button, count);
    }

    @Override
    public void mouseDrag(int x, int y, int nx, int ny, int button) {
        if (scrollBar.isWithinBounds(x, y)) {
            scrollBar.mouseDrag(x, y, nx, ny, button);
            return;
        }
        super.mouseDrag(x, y, nx, ny, button);
    }

    @Override
    public void mouseMove(int x, int y) {
        super.mouseMove(x, y);
    }

    @Override
    public boolean keyPress(int keyCode, char character) {
        return super.keyPress(keyCode, character);
    }

    @Override
    public boolean keyRelease(int keyCode, char character) {
        return super.keyRelease(keyCode, character);
    }

    @Override
    public void remove(GuiComponent child) {
        this.viewport.remove(child);
    }

    @Override
    public boolean mouseWheel(int x, int y, double rotation, int amount, int units) {
        x -= this.x + this.innerXOffset;
        y -= this.y + this.innerYOffset;
        if (viewport.isWithinBounds(x, y) && viewport.mouseWheel(x, y, rotation, amount, units)) {
            return true;
        }
        this.viewport.setYScroll(this.viewport.getYScroll() + rotation * 100 * amount / 10);
        return true;
    }
}
