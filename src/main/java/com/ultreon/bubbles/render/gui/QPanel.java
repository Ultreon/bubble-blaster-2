package com.ultreon.bubbles.render.gui;

import com.ultreon.bubbles.render.Renderer;

public class QPanel extends QContainer {
    @Override
    public void renderComponents(Renderer renderer) {
        components.forEach(c -> c.render(renderer));
    }

    @Override
    public void render(Renderer renderer) {
        Renderer ngg2 = renderer.subInstance(getX(), getY(), getWidth(), getHeight());
        renderComponent(ngg2);
        ngg2.dispose();

        Renderer ngg3 = renderer.subInstance(getX(), getY(), getWidth(), getHeight());
        renderComponents(ngg3);
        ngg3.dispose();
    }

    @Override
    public void renderComponent(Renderer renderer) {
        renderer.color(getBackgroundColor());
        renderer.fill(getBounds());
    }

    @Override
    public void tick() {

    }
}
