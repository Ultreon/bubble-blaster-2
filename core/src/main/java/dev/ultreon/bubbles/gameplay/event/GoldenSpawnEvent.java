package dev.ultreon.bubbles.gameplay.event;

import dev.ultreon.bubbles.common.gamestate.GameplayContext;
import dev.ultreon.bubbles.common.gamestate.GameplayEvent;
import dev.ultreon.bubbles.data.DataKeys;
import dev.ultreon.bubbles.event.v1.VfxEffectBuilder;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.ubo.types.IntType;

import static dev.ultreon.bubbles.BubbleBlaster.NAMESPACE;

public class GoldenSpawnEvent extends GameplayEvent {
    @Override
    public void buildVfx(VfxEffectBuilder builder) {

    }

    @Override
    public void begin(World world) {
        super.begin(world);

        world.setStateDifficultyModifier(this, 0.1f);
        world.getGameplayStorage().get(NAMESPACE).putBoolean(DataKeys.GOLDEN_SPAWN_ACTIVE, true);
    }

    @Override
    public void end(World world) {
        super.end(world);

        world.setStateDifficultyModifier(this, 1f);
        world.getGameplayStorage().get(NAMESPACE).putBoolean(DataKeys.GOLDEN_SPAWN_ACTIVE, false);
        world.getGameplayStorage().get(NAMESPACE).putInt(DataKeys.GOLDEN_SPAWN_COUNTER, world.getSubRandom(this).nextInt(48000, 96000) + 1);
    }

    @Override
    public void renderBackground(World world, Renderer renderer) {
        renderer.fillGradient(0, 0, renderer.getWidth(), renderer.getHeight(), Color.rgb(0xffe000), Color.rgb(0xffb000));
    }

    @Override
    public boolean shouldActivate(GameplayContext context) {
        if (!context.gameplayStorage().get(NAMESPACE).<IntType>contains(DataKeys.GOLDEN_SPAWN_COUNTER)) {
            context.gameplayStorage().get(NAMESPACE).putInt(DataKeys.GOLDEN_SPAWN_COUNTER, context.world().getSubRandom(this).nextInt(48000, 96000) + 1);
            return false;
        }
        int anInt = context.gameplayStorage().get(NAMESPACE).getInt(DataKeys.GOLDEN_SPAWN_COUNTER);
        anInt -= 5;
        context.gameplayStorage().get(NAMESPACE).putInt(DataKeys.GOLDEN_SPAWN_COUNTER, anInt);
        boolean b = anInt <= 0;
        context.gameplayStorage().get(NAMESPACE).putInt(DataKeys.GOLDEN_SPAWN_COUNTER, context.world().getSubRandom(this).nextInt(150, 250));
        return b;
    }

    @Override
    public boolean shouldContinue(GameplayContext context) {
        int anInt = context.gameplayStorage().get(NAMESPACE).getInt(DataKeys.GOLDEN_SPAWN_COUNTER);
        anInt -= 5;
        context.gameplayStorage().get(NAMESPACE).putInt(DataKeys.GOLDEN_SPAWN_COUNTER, anInt);
        return anInt > 0;
    }
}
