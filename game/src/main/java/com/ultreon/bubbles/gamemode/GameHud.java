package com.ultreon.bubbles.gamemode;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;

/**
 * HUD Baseclass
 * The baseclass for all HUD's.
 *
 * @see ClassicModeHud
 * @see Gamemode
 */
public abstract class GameHud extends Screen {
    private final Gamemode gamemode;

    public GameHud(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    @SuppressWarnings("EmptyMethod")
    public void tick() {

    }

    @Override
    public final void init() {

    }

    public void render(Renderer renderer) {

    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public void onLevelUp(int to) {

    }
}
