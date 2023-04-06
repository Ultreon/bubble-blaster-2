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
        this.viewport = viewport;
        this.scrollBar = new ScrollBar(x + width - SCROLLBAR_WIDTH, y, SCROLLBAR_WIDTH, height);
    }

    @Override
    public void renderChildren(Renderer renderer) {
        this.viewport.setWidth(getWidth() - SCROLLBAR_WIDTH);
        this.viewport.setHeight(getHeight());
        this.viewport.setX(0);
        this.viewport.setY(0);
        this.viewport.render(renderer);
        this.scrollBar.setWidth(SCROLLBAR_WIDTH);
        this.scrollBar.setHeight(getHeight());
        this.scrollBar.setX(getWidth() - SCROLLBAR_WIDTH);
        this.scrollBar.setY(0);
        this.scrollBar.render(renderer);
    }

    @Override
    public void render(Renderer renderer) {
        this.updateXYOffset();
        this.scrollBar.setPercent(this.viewport.getYPercent());
        this.scrollBar.setScale(1 - (((double) this.viewport.getViewportSize().height - (double) this.viewport.getHeight()) / (double) this.viewport.getViewportSize().height));

        Renderer componentRenderer = renderer.subInstance(getX(), getY(), getWidth(), getHeight());
        renderComponent(componentRenderer);
        componentRenderer.dispose();
        Renderer childRenderer = renderer.subInstance(getX(), getY(), getWidth(), getHeight());
        renderChildren(childRenderer);
        childRenderer.dispose();
    }

    private void updateXYOffset() {
        this.innerXOffset = (int) -this.viewport.xScroll;
        this.innerYOffset = (int) -this.viewport.yScroll;
    }

    @Override
    public void renderComponent(Renderer renderer) {
        renderer.color(this.getBackgroundColor());
        renderer.fill(this.getBounds());
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
    public void remove(GuiComponent child) {
        this.viewport.remove(child);
    }

    @Override
    public void mouseWheel(int x, int y, double rotation, int amount, int units) {
        this.viewport.setYScroll(this.viewport.getYScroll() + rotation * 100 * amount / 10);
    }
}
