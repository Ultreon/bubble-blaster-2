package dev.ultreon.bubbles.render;


public abstract class BBTexture {
    public void draw(Renderer renderer, int x, int y, int width, int height) {
        this.draw(renderer, x, y, width, height, 0, 0, this.getWidth(), this.getHeight());
    }

    protected abstract int getWidth();

    protected abstract int getHeight();

    public abstract void draw(Renderer renderer, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight);
}
