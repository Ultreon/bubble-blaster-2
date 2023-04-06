package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.bubbles.render.gui.Renderable;
import org.jetbrains.annotations.Nullable;
import org.checkerframework.common.value.qual.IntRange;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Container extends GuiComponent {
    protected final List<GuiComponent> children = new CopyOnWriteArrayList<>();
    protected final List<Renderable> statics = new CopyOnWriteArrayList<>();
    protected GuiComponent hoveredInteractable;

    public Container(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Renderer renderer) {
        Renderer containment = renderer.subInstance(this.x, this.y, this.width, this.height);
        renderChildren(containment);
    }

    protected void renderChildren(Renderer renderer) {
        for (GuiComponent child : this.children) {
            if (child.visible) {
                child.render(renderer);
            }
        }
    }

    /**
     * Adds a {@link GuiComponent gui component } to the screen, including initializing it with {@link GuiStateListener#make()}.
     *
     * @param child the gui component to add.
     * @param <T>   the component's type.
     * @return the same as the parameter.
     */
    public <T extends GuiComponent> T add(T child) {
        this.children.add(child);
        child.make();
        return child;
    }

    public void remove(GuiComponent child) {
        this.children.remove(child);
    }

    @Nullable
    public GuiComponent getWidgetAt(int x, int y) {
        for (GuiComponent child : this.children) {
            if (!child.enabled || !child.visible) continue;
            if (child.isWithinBounds(x, y)) return child;
        }
        return null;
    }

    @Override
    public boolean mouseClick(int x, int y, int button, int count) {
        GuiComponent inputWidget = getWidgetAt(x, y);
        return inputWidget != null && inputWidget.mouseClick(x, y, button, count);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        GuiComponent inputWidget = getWidgetAt(x, y);
        return inputWidget != null && inputWidget.mousePress(x, y, button);
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        GuiComponent inputWidget = getWidgetAt(x, y);
        return inputWidget != null && inputWidget.mouseRelease(x, y, button);
    }

    @Override
    public void mouseMove(int x, int y) {
        boolean widgetChanged = false;
        if (this.hoveredInteractable != null && !this.hoveredInteractable.isWithinBounds(x, y)) {
            this.hoveredInteractable.mouseExit();
        }

        GuiComponent inputWidgetAt = this.getWidgetAt(x, y);
        if (inputWidgetAt != this.hoveredInteractable) widgetChanged = true;
        this.hoveredInteractable = inputWidgetAt;

        if (this.hoveredInteractable != null) {
            this.hoveredInteractable.mouseMove(x, y);

            if (widgetChanged) {
                this.hoveredInteractable.mouseEnter(x, y);
            }
        }
    }

    @Override
    public void mouseDrag(int x, int y, int nx, int ny, int button) {
        GuiComponent inputWidget = getWidgetAt(x, y);
        if (inputWidget != null) inputWidget.mouseDrag(x, y, nx, ny, button);
    }

    @Override
    public void mouseExit() {
        if (this.hoveredInteractable != null) {
            this.hoveredInteractable.mouseExit();
            this.hoveredInteractable = null;
        }
    }

    @Override
    public void mouseWheel(int x, int y, double rotation, int amount, int units) {
        GuiComponent inputWidget = getWidgetAt(x, y);
        if (inputWidget != null) inputWidget.mouseWheel(x, y, rotation, amount, units);
    }

    protected final void clearWidgets() {
        for (GuiComponent widget : children) {
            widget.destroy();
        }
        children.clear();
    }

    public GuiComponent getHoveredWidget() {
        return hoveredInteractable;
    }
}
