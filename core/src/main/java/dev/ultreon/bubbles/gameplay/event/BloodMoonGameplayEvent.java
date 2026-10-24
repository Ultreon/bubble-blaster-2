package dev.ultreon.bubbles.gameplay.event;

import com.badlogic.gdx.graphics.Color;
import com.crashinvaders.vfx.effects.FilmGrainEffect;
import dev.ultreon.bubbles.Axis2D;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.BubbleBlasterConfig;
import dev.ultreon.bubbles.common.Difficulty;
import dev.ultreon.bubbles.common.gamestate.GameplayContext;
import dev.ultreon.bubbles.common.gamestate.GameplayEvent;
import dev.ultreon.bubbles.data.DataKeys;
import dev.ultreon.bubbles.event.v1.VfxEffectBuilder;
import dev.ultreon.bubbles.random.JavaRandom;
import dev.ultreon.bubbles.random.RandomSource;
import dev.ultreon.bubbles.render.Colors;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.bubbles.world.WorldRenderer;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.UUID;

import static dev.ultreon.bubbles.BubbleBlaster.NAMESPACE;
import static dev.ultreon.bubbles.BubbleBlaster.TPS;

public class BloodMoonGameplayEvent extends GameplayEvent {
    private static final UUID NOISE_EFFECT_ID = UUID.fromString("7d6dfafe-bbe6-4795-bc09-8c778af55115");
    private static final Color UPPER_COLOR = Colors.rgb(0xff2010);
    private static final Difficulty.ModifierToken MODIFIER_TOKEN = new Difficulty.ModifierToken();
    private static final Marker MARKER = MarkerFactory.getMarker("BloodMoon");

    private final RandomSource randomSource = new JavaRandom();
    private long deactivateTicks;

    public BloodMoonGameplayEvent() {
        super();
    }

    @Override
    public void begin(World world) {
        super.begin(world);

        this.deactivateTicks = (long) this.randomSource.nextInt(BubbleBlasterConfig.BLOOD_MOON_STOP_LOW.get(), BubbleBlasterConfig.BLOOD_MOON_STOP_HIGH.get()) * TPS;

        world.getGameplayStorage().get(NAMESPACE).putBoolean(DataKeys.BLOOD_MOON_ACTIVE, false);

        world.getDifficultyModifiers().set(MODIFIER_TOKEN, new Difficulty.Modifier(Difficulty.ModifierAction.MULTIPLY, 8));
        BubbleBlaster.LOGGER.info(MARKER, "Blood moon started for {} ticks ({} secs)", this.deactivateTicks, this.deactivateTicks / TPS);
    }

    @Override
    public void tick() {
        if (this.deactivateTicks-- < 0)
            this.deactivateTicks = -1;
    }

    @Override
    public void end(World world) {
        super.end(world);

        this.deactivateTicks = -1;

        world.updateNextBloodMoon();
        world.getDifficultyModifiers().remove(MODIFIER_TOKEN);
        BubbleBlaster.LOGGER.info(MARKER, "Blood moon ended, {} ticks ({} secs) left.", this.deactivateTicks, this.deactivateTicks / TPS);
    }

    @Override
    public void buildVfx(VfxEffectBuilder builder) {
        var effect = new FilmGrainEffect();
        effect.setNoiseAmount(0.4f);
        builder.set(NOISE_EFFECT_ID, effect);
    }

    @Override
    public final boolean shouldActivate(GameplayContext context) {
        if (!super.shouldActivate(context)) return false;

        var storage = context.gameplayStorage().get(BubbleBlaster.NAMESPACE);
        return storage.getBoolean(DataKeys.BLOOD_MOON_ACTIVE, false);
    }

    @Override
    public final boolean shouldContinue(GameplayContext context) {
        if (!super.shouldContinue(context)) return false;

        var storage = context.gameplayStorage().get(BubbleBlaster.NAMESPACE);
        return storage.getBoolean(DataKeys.BLOOD_MOON_ACTIVE, false);
    }

    @Override
    public void renderBackground(World world, Renderer renderer) {
        var instance = BubbleBlaster.getInstance();
        var bounds = instance.getBounds();
        renderer.fillGradient(bounds, WorldRenderer.BG_TOP, WorldRenderer.BG_BOTTOM, Axis2D.VERTICAL);
        bounds.height *= 5f / 4f;
        renderer.fillGradient(bounds, UPPER_COLOR, Colors.TRANSPARENT, Axis2D.VERTICAL);
    }

    public void deactivate() {
        this.deactivateTicks = -1;
    }

    public long getDeactivateTicks() {
        return this.deactivateTicks;
    }
}
