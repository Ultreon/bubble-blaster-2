package com.ultreon.bubbles.common.gamestate;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v1.VfxEffectBuilder;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused"})
public abstract class GameplayEvent {
    private Color backgroundColor;
    private final BubbleBlaster game = BubbleBlaster.getInstance();

    public GameplayEvent() {

    }

    public boolean shouldActivate(GameplayContext context) {
        @Nullable Screen currentScreen = game.getCurrentScreen();
        if (currentScreen != null) return false;

        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        return loadedGame != null;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldContinue(GameplayContext context) {
        return BubbleBlaster.getInstance().getLoadedGame() != null;
    }

    public abstract void buildVfx(VfxEffectBuilder builder);

    public void renderBackground(Environment environment, Renderer renderer) {

    }

    @Override
    public String toString() {
        return "GameEvent[" + Registries.GAMEPLAY_EVENTS.getKey(this) + "]";
    }
}
