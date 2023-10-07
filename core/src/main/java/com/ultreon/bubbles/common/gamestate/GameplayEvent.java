package com.ultreon.bubbles.common.gamestate;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.event.v1.VfxEffectBuilder;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.text.v1.Translatable;
import org.jetbrains.annotations.Nullable;

public abstract class GameplayEvent implements Translatable {
    protected final BubbleBlaster game = BubbleBlaster.getInstance();
    private Color backgroundColor;

    public GameplayEvent() {

    }

    public boolean shouldActivate(GameplayContext context) {
        @Nullable Screen currentScreen = this.game.getCurrentScreen();
        if (currentScreen != null) return false;

        var loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        return loadedGame != null;
    }

    public void begin(World world) {

    }

    public void tick() {

    }

    public void end(World world) {

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldContinue(GameplayContext context) {
        return BubbleBlaster.getInstance().getLoadedGame() != null;
    }

    public abstract void buildVfx(VfxEffectBuilder builder);

    public void renderBackground(World world, Renderer renderer) {

    }

    @Override
    public String toString() {
        return "GameEvent[" + Registries.GAMEPLAY_EVENTS.getKey(this) + "]";
    }

    @Override
    public String getTranslationPath() {
        var id = this.getId();
        return id.location() + ".gameplayEvent." + id.path().replaceAll("/", ".");
    }

    private Identifier getId() {
        return Registries.GAMEPLAY_EVENTS.getKey(this);
    }
}
