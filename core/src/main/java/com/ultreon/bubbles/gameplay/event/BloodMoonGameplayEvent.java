package com.ultreon.bubbles.gameplay.event;

import com.crashinvaders.vfx.effects.FilmGrainEffect;
import com.ultreon.bubbles.Axis2D;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.common.gamestate.GameplayContext;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.data.DataKeys;
import com.ultreon.bubbles.event.v1.VfxEffectBuilder;
import com.ultreon.bubbles.random.JavaRandom;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.time.Date;
import com.ultreon.commons.time.DateTime;
import com.ultreon.commons.time.Time;
import com.ultreon.data.types.MapType;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.UUID;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

@SuppressWarnings("unused")
public class BloodMoonGameplayEvent extends GameplayEvent {
    private static final UUID NOISE_EFFECT_ID = UUID.fromString("7d6dfafe-bbe6-4795-bc09-8c778af55115");
    private static final Color UPPER_COLOR = Color.rgb(0xff3000);
    private static final Color LOWER_COLOR = Color.CRIMSON;
    private static final Difficulty.ModifierToken MODIFIER_TOKEN = new Difficulty.ModifierToken();
    private static final Marker MARKER = MarkerFactory.getMarker("BloodMoon");
    private final Date date = new Date(31, 10, 0);
    private final Time timeLo = new Time(3, 0, 0);
    private final Time timeHi = new Time(3, 59, 59);

    private final RandomSource randomSource = new JavaRandom();
    private long deactivateTicks;
    private Instant nextActivate;

    public BloodMoonGameplayEvent() {
        super();
    }

    @Override
    public void begin(World world) {
        super.begin(world);

        this.deactivateTicks = (long) this.randomSource.nextInt(BubbleBlasterConfig.BLOOD_MOON_STOP_LOW.get(), BubbleBlasterConfig.BLOOD_MOON_STOP_HIGH.get()) * TPS;

        world.getDifficultyModifiers().set(MODIFIER_TOKEN, new Difficulty.Modifier(Difficulty.ModifierAction.ADD, 2));
        BubbleBlaster.LOGGER.info(MARKER, "Blood moon started for " + this.deactivateTicks + " ticks (" + (this.deactivateTicks / TPS) + " secs)");
    }

    public void tick() {
        if (this.deactivateTicks-- < 0) {
            this.deactivateTicks = -1;
        }
    }

    @Override
    public void end(World world) {
        super.end(world);

        this.resetNext();

        world.getDifficultyModifiers().remove(MODIFIER_TOKEN);
        BubbleBlaster.LOGGER.info(MARKER, "Blood moon ended, " + this.deactivateTicks + " ticks (" + (this.deactivateTicks / TPS) + " secs) left.");
    }

    public void resetNext() {
        this.nextActivate = Instant.now().plusSeconds((long) this.randomSource.nextInt(BubbleBlasterConfig.BLOOD_MOON_TRIGGER_LOW.get(), BubbleBlasterConfig.BLOOD_MOON_TRIGGER_HIGH.get()) * TPS);
    }

    @Override
    public void buildVfx(VfxEffectBuilder builder) {
        FilmGrainEffect effect = new FilmGrainEffect();
        effect.setNoiseAmount(0.4f);
        builder.set(NOISE_EFFECT_ID, effect);
    }

    @Override
    public final boolean shouldActivate(GameplayContext context) {
        if (!super.shouldActivate(context)) return false;

        if (Date.current().equalsIgnoreYear(this.date)) return true;
        if (Time.current().isBetween(this.timeLo, this.timeHi)) return true;

        if (Instant.now().isAfter(this.nextActivate)) return true;

        MapType storage = context.gameplayStorage().get(BubbleBlaster.NAMESPACE);
        return storage.getBoolean(DataKeys.BLOOD_MOON_ACTIVE);
    }

    @Override
    public final boolean shouldContinue(GameplayContext context) {
        if (!super.shouldContinue(context)) return false;

        if (Date.current().equalsIgnoreYear(this.date)) return true;
        if (Time.current().isBetween(this.timeLo, this.timeHi)) return true;

        if (this.deactivateTicks > 0) return true;

        MapType storage = context.gameplayStorage().get(BubbleBlaster.NAMESPACE);
        return storage.getBoolean(DataKeys.BLOOD_MOON_ACTIVE);
    }

    @Override
    public void renderBackground(World world, Renderer renderer) {
        BubbleBlaster instance = BubbleBlaster.getInstance();
        renderer.fillGradient(instance.getBounds(), UPPER_COLOR, LOWER_COLOR, Axis2D.VERTICAL);
    }

    public final boolean wouldActive(DateTime dateTime) {
        boolean flag1 = dateTime.getTime().isBetween(this.timeLo, this.timeHi);  // Devil's hour.
        boolean flag2 = dateTime.getDate().equalsIgnoreYear(this.date);  // Halloween.

        boolean flag3 = dateTime.getDate().getDayOfWeek() == DayOfWeek.FRIDAY;  // Friday
        boolean flag4 = dateTime.getDate().getDay() == 13;  // 13th

        return (flag1 && flag2) || (flag3 && flag4);  // Every October 31st in devil's hour. Or Friday 13th.
    }

    public void deactivate() {
    }

    public void activate() {
    }

}
