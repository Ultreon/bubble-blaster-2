package com.ultreon.bubbles.render.gui.hud;

import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.common.Controllable;
import com.ultreon.bubbles.world.World;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;

/**
 * HUD Baseclass
 * The baseclass for all HUD's.
 *
 * @see ClassicHud
 * @see Gamemode
 */
public abstract class HudType extends Screen implements Controllable {
    private static HudType current;

    public static HudType getCurrent() {
        return current;
    }

    public static void setCurrent(HudType current) {
        HudType.current = current;
    }

    @Override
    public final void init() {

    }

    @Override
    public final void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        World world = this.game.world;
        if (world != null) {
            this.renderHudOverlay(renderer, world, world.getGamemode(), deltaTime);
        }
    }

    public void renderHudOverlay(Renderer renderer, World world, Gamemode gamemode, float deltaTime) {

    }

    public void drawMessages(Renderer renderer) {
        LoadedGame loadedGame = this.game.getLoadedGame();
        if (loadedGame != null) {
            loadedGame.drawMessages(this.game, renderer);
        }
    }

    public void onLevelUp(int newLevel) {

    }

    /**
     * @param renderer        the game renderer, used to draw stuff on the screen.
     * @param origColorTop    the original top color.
     * @param origColorBottom the original bottom color.
     * @return true to cancel the default background rendering.
     */
    public boolean renderBackground(Renderer renderer, Color origColorTop, Color origColorBottom) {
        return false;
    }

    public void begin() {
        
    }

    public void end() {
        
    }
}
