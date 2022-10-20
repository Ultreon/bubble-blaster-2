package com.ultreon.bubbles.render.screen.gui;

import com.ultreon.bubbles.render.Renderer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.IntRange;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Container extends InputWidget {
    protected final List<InputWidget> children = new CopyOnWriteArrayList<>();
    protected final List<StaticWidget> statics = new CopyOnWriteArrayList<>();
    protected InputWidget hoveredInteractable;

    public Container(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Renderer renderer) {
        Renderer containment = renderer.subInstance(this.x, this.y, this.width, this.height);
        renderChildren(containment);
    }

    protected void renderChildren(Renderer renderer) {
        for (InputWidget child : this.children) {
            child.render(renderer);
        }
    }

    public <T extends InputWidget> T add(T child) {
        this.children.add(child);
        return child;
    }

    public void remove(InputWidget child) {
        this.children.remove(child);
    }

    @Nullable
    public InputWidget getWidgetAt(int x, int y) {
//        logger.info("Container[c7f17d76]: CHILDREN" + this.children);
        for (InputWidget child : this.children) {
//            logger.info("Container[b610e134]: X(" + x + ") : Y(" + y + ") : CONTAINS(" + child.getX() + "," + child.getY() + "," + child.isWithinBounds(x, y) + ")");
            if (child.isWithinBounds(x, y)) return child;
        }
        return null;
    }

    @Override
    public boolean mouseClick(int x, int y, int button, int count) {
        InputWidget inputWidget = getWidgetAt(x, y);
        return inputWidget != null && inputWidget.mouseClick(x, y, button, count);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        InputWidget inputWidget = getWidgetAt(x, y);
        return inputWidget != null && inputWidget.mousePress(x, y, button);
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        InputWidget inputWidget = getWidgetAt(x, y);
        return inputWidget != null && inputWidget.mouseRelease(x, y, button);
    }

    @Override
    public void mouseMove(int x, int y) {
        boolean widgetChanged = false;
        if (this.hoveredInteractable != null && !this.hoveredInteractable.isWithinBounds(x, y)) {
            this.hoveredInteractable.mouseExit();
        }

        InputWidget inputWidgetAt = this.getWidgetAt(x, y);
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
        InputWidget inputWidget = getWidgetAt(x, y);
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
        InputWidget inputWidget = getWidgetAt(x, y);
        if (inputWidget != null) inputWidget.mouseWheel(x, y, rotation, amount, units);
    }

    protected final void clearWidgets() {
        for (InputWidget widget : children) {
            widget.destroy();
        }
        children.clear();
    }

    public InputWidget getHoveredWidget() {
        return hoveredInteractable;
    }
}
