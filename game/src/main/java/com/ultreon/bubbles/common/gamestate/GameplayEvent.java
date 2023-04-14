package com.ultreon.bubbles.common.gamestate;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.LoadedGame;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.commons.time.DateTime;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused"})
public abstract class GameplayEvent {
    private Color backgroundColor;
    private final BubbleBlaster game = BubbleBlaster.getInstance();

    public GameplayEvent() {

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isActive(DateTime dateTime) {
        @Nullable Screen currentScreen = game.getCurrentScreen();

        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        if (loadedGame == null) {
            return false;
        }

        return loadedGame.getEnvironment().isGameStateActive(this);
    }

    public final Color getBackgroundColor() {
        return backgroundColor;
    }

    public void renderBackground(BubbleBlaster game, Renderer renderer) {
        if (backgroundColor == null) return;
        if (!isActive(DateTime.current())) return;

        renderer.color(getBackgroundColor());
        renderer.fill(BubbleBlaster.getInstance().getGameBounds());
    }

    public final void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    @Override
    public String toString() {
        return "GameEvent[" + Registry.GAMEPLAY_EVENTS.getKey(this) + "]";
    }
}
