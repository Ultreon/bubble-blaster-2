package dev.ultreon.bubbles.render;

public interface ITexture {
    void render(Renderer renderer);

    int width();

    int height();
}
