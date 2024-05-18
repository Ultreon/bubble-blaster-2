package dev.ultreon.bubbles.render.gui.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.common.Controllable;
import dev.ultreon.bubbles.gamemode.Gamemode;
import dev.ultreon.bubbles.init.Fonts;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.screen.CommandScreen;
import dev.ultreon.bubbles.world.World;

/**
 * HUD Baseclass
 * The baseclass for all HUD's.
 *
 * @see BetaHud
 * @see Gamemode
 */
public abstract class HudType implements Controllable {
    private static HudType current;
    protected BubbleBlaster game = BubbleBlaster.getInstance();
    protected BitmapFont font = Fonts.DEFAULT.get();

    public static HudType getCurrent() {
        return current;
    }

    public static void setCurrent(HudType current) {
        HudType.current = current;
    }

    public final void init() {

    }

    public void renderHudOverlay(Renderer renderer, World world, Gamemode gamemode, float deltaTime) {

    }

    public void drawMessages(Renderer renderer) {
        var loadedGame = this.game.getLoadedGame();
        if (loadedGame != null && !this.game.hasScreenOpen()) {
            CommandScreen.drawMessages(renderer, this.game.getHeight());
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

    @Override
    public void begin() {
        
    }

    @Override
    public void end() {
        
    }

    protected float width() {
        return Gdx.graphics.getWidth();
    }

    protected int height() {
        return Gdx.graphics.getHeight();
    }
}
