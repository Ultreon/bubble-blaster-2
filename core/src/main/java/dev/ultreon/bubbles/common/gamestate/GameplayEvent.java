package dev.ultreon.bubbles.common.gamestate;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.event.v1.VfxEffectBuilder;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.screen.Screen;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.text.v1.Translatable;
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
