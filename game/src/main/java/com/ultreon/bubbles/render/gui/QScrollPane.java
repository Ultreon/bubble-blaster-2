package com.ultreon.bubbles.render.gui;

import com.google.common.annotations.Beta;
import com.ultreon.bubbles.render.Renderer;

@Beta
public class QScrollPane extends QContainer {
    private QViewport viewport;

    @Override
    public void renderComponents(Renderer renderer) {

    }

    @Override
    public void render(Renderer renderer) {

    }

    @Override
    public void renderComponent(Renderer renderer) {

    }

    @Override
    public void tick() {

    }

    public QViewport getViewport() {
        return viewport;
    }

    public void setViewport(QViewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public QComponent add(QComponent component) {
        this.viewport.components.add(component);
        return component;
    }

    @Override
    public QComponent remove(QComponent component) {
        this.viewport.components.remove(component);
        return component;
    }
}
