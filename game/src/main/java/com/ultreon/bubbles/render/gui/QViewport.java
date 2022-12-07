package com.ultreon.bubbles.render.gui;

import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

public class QViewport extends QContainer {
    private final Rectangle viewportRect;

    public QViewport(Rectangle viewportRect) {
        this.viewportRect = viewportRect;
        setBackgroundColor(new Color(64, 64, 64));
    }

    @Override
    public void renderComponents(Renderer renderer) {
        for (QComponent component : components) {
            Renderer componentGraphics = renderer.subInstance(component.getX(), component.getY(), component.getWidth(), component.getHeight());
            component.render(componentGraphics);
            componentGraphics.dispose();
        }
    }

    @Override
    public void render(Renderer renderer) {
        this.renderComponent(renderer);

        Renderer viewportGraphics = renderer.subInstance(viewportRect.x, viewportRect.y, viewportRect.width, viewportRect.height);
        renderComponents(viewportGraphics);
    }

    @Override
    public void renderComponent(Renderer renderer) {
        renderer.color(getBackgroundColor());
        renderer.rect(0, 0, getSize().width, getSize().height);
    }

    @Override
    public void tick() {

    }

    public void setViewportSize(Dimension size) {
        this.viewportRect.setSize(size);
    }

    public Dimension getViewportSize() {
        return viewportRect.getSize();
    }

    public void setViewportLocation(Point location) {
        this.viewportRect.setLocation(location);
    }

    public Point getViewportLocation() {
        return viewportRect.getLocation();
    }
}
