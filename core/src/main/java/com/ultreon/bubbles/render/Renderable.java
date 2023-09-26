package com.ultreon.bubbles.render;

public interface Renderable {
    /**
     * Rendering method, should not be called if you don't know what you are doing.
     *
     * @param renderer  renderer to draw/render with.
     * @param mouseX
     * @param mouseY
     * @param deltaTime
     */
    void render(Renderer renderer, int mouseX, int mouseY, float deltaTime);
}
