package com.ultreon.bubbles.render;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GameWindow;

public class RenderSettings {
    private final boolean antialiasingBackup = BubbleBlaster.getInstance().isAntialiasEnabled();
    private final GameWindow window = BubbleBlaster.getInstance().getGameWindow();
    private boolean antialiasingOverride = antialiasingBackup;

    public boolean isAntialiasingEnabled() {
        return antialiasingOverride;
    }

    public void setAntialiasing(boolean antialiasing) {
        antialiasingOverride = antialiasing;
    }

    public void resetAntialiasing() {
        antialiasingOverride = antialiasingBackup;
    }

    public void enableAntialiasing() {
        antialiasingOverride = true;
    }

    public void disableAntialiasing() {
        antialiasingOverride = false;
    }

    public float getScale() {
        if (BubbleBlaster.getInstance() == null) {
            return 1.f;
        }
        int width = window.getWidth();
        int height = window.getHeight();

        return width > height ? width / 600f : height / 600f;
    }

    @SuppressWarnings("EmptyMethod")
    public void drawBubble() {

    }
}
