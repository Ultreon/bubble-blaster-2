package com.ultreon.bubbles.gamemode;

import com.ultreon.bubbles.render.Renderer;

/**
 * HUD Baseclass
 * The baseclass for all HUD's.
 *
 * @see ClassicModeHud
 * @see Gamemode
 */
public abstract class GameHud {
    private final Gamemode gamemode;

    public GameHud(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    @SuppressWarnings("EmptyMethod")
    public void tick() {

    }

    public void render(Renderer renderer) {

    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public void onLevelUp(int to) {

    }
}
