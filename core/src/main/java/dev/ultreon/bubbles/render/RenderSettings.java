package dev.ultreon.bubbles.render;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.GameWindow;

public class RenderSettings {
    private final boolean antialiasingBackup = BubbleBlaster.getInstance().isAntialiasEnabled();
    private final GameWindow window = BubbleBlaster.getInstance().getGameWindow();
    private boolean antialiasingOverride = this.antialiasingBackup;

    public boolean isAntialiasingEnabled() {
        return this.antialiasingOverride;
    }

    public void setAntialiasing(boolean antialiasing) {
        this.antialiasingOverride = antialiasing;
    }

    public void resetAntialiasing() {
        this.antialiasingOverride = this.antialiasingBackup;
    }

    public void enableAntialiasing() {
        this.antialiasingOverride = true;
    }

    public void disableAntialiasing() {
        this.antialiasingOverride = false;
    }

    public float getScale() {
        if (BubbleBlaster.getInstance() == null) {
            return 1.f;
        }
        var width = this.window.getWidth();
        var height = this.window.getHeight();

        return width > height ? width / 600f : height / 600f;
    }

    @SuppressWarnings("EmptyMethod")
    public void drawBubble() {

    }
}
