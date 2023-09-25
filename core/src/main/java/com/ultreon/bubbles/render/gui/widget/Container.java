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
    int innerXOffset;
    int innerYOffset;
    private GuiComponent pressingWidget;

    public Container(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Renderer renderer) {
        renderChildren(renderer);
    }

    protected void renderChildren(Renderer renderer) {
        for (GuiComponent child : this.children) {
            if (child.visible) {
                renderer.subInstance(child.getX(), child.getY(), child.getWidth(), child.getHeight(), child::render);
            }
        }
    }

    @Nullable
    public GuiComponent getExactWidgetAt(int x, int y) {
        GuiComponent widgetAt = getWidgetAt(x, y);
        if (widgetAt instanceof Container container) {
            return container.getExactWidgetAt(x, y);
        }
        return widgetAt;
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
        List<GuiComponent> guiComponents = this.children;
        for (int i = guiComponents.size() - 1; i >= 0; i--) {
            GuiComponent child = guiComponents.get(i);
            if (!child.enabled || !child.visible) continue;
            if (child.isWithinBounds(x, y)) return child;
        }
        return null;
    }

    @Override
    public boolean mouseClick(int x, int y, int button, int count) {
        GuiComponent widgetAt = getWidgetAt(x, y);
        return widgetAt != null && widgetAt.mouseClick(x - widgetAt.getX(), y - widgetAt.getY(), button, count);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        GuiComponent widgetAt = getWidgetAt(x, y);
        x -= this.x + this.innerXOffset;
        y -= this.y + this.innerYOffset;
        pressingWidget = widgetAt;
        return widgetAt != null && widgetAt.mousePress(x - widgetAt.getX(), y - widgetAt.getY(), button);
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        GuiComponent widgetAt = pressingWidget;
        x -= this.x + this.innerXOffset;
        y -= this.y + this.innerYOffset;
        return widgetAt != null && widgetAt.mouseRelease(x - widgetAt.getX(), y - widgetAt.getY(), button);
    }

    @Override
    public void mouseMove(int x, int y) {
        GuiComponent widgetAt = this.getWidgetAt(x, y);
        boolean widgetChanged = false;
        if (this.hoveredInteractable != null && !this.hoveredInteractable.isWithinBounds(x, y)) {
            this.hoveredInteractable.mouseExit();
        }

        if (widgetAt != this.hoveredInteractable) widgetChanged = true;
        this.hoveredInteractable = widgetAt;

        if (this.hoveredInteractable != null) {
            x -= this.x + this.innerXOffset;
            y -= this.y + this.innerYOffset;
            this.hoveredInteractable.mouseMove(x - widgetAt.getX(), y - widgetAt.getY());

            if (widgetChanged) {
                this.hoveredInteractable.mouseEnter(x - widgetAt.getX(), y - widgetAt.getY());
            }
        }
        super.mouseMove(x, y);
    }

    @Override
    public void mouseEnter(int x, int y) {
        GuiComponent widgetAt = this.getWidgetAt(x, y);
        boolean widgetChanged = false;
        if (this.hoveredInteractable != null && !this.hoveredInteractable.isWithinBounds(x, y)) {
            this.hoveredInteractable.mouseExit();
        }

        if (widgetAt != this.hoveredInteractable) widgetChanged = true;
        this.hoveredInteractable = widgetAt;

        if (this.hoveredInteractable != null) {
            x -= this.x + this.innerXOffset;
            y -= this.y + this.innerYOffset;
            if (widgetChanged) {
                this.hoveredInteractable.mouseEnter(x - widgetAt.getX(), y - widgetAt.getY());
            }
        }
        super.mouseMove(x, y);
    }

    @Override
    public void mouseDrag(int x, int y, int nx, int ny, int button) {
        GuiComponent widgetAt = getWidgetAt(x, y);
        x -= this.x + this.innerXOffset;
        y -= this.y + this.innerYOffset;
        nx -= this.x + this.innerXOffset;
        ny -= this.y + this.innerYOffset;
        if (widgetAt != null) widgetAt.mouseDrag(x - widgetAt.getX(), y - widgetAt.getY(), nx, ny, button);
    }

    @Override
    public void mouseExit() {
        if (this.hoveredInteractable != null) {
            this.hoveredInteractable.mouseExit();
            this.hoveredInteractable = null;
        }
    }

    @Override
    public boolean mouseWheel(int x, int y, float rotation) {
        GuiComponent widgetAt = getWidgetAt(x, y);
        x -= this.x + this.innerXOffset;
        y -= this.y + this.innerYOffset;
        if (widgetAt != null) return widgetAt.mouseWheel(x - widgetAt.getX(), y - widgetAt.getY(), rotation);
        return false;
    }

    protected final void clearWidgets() {
        for (GuiComponent widget : this.children) {
            widget.destroy();
        }
        this.children.clear();
    }

    public GuiComponent getHoveredWidget() {
        return hoveredInteractable;
    }
}
