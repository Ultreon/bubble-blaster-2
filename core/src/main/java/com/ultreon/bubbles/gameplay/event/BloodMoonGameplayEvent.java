package com.ultreon.bubbles.gameplay.event;

import com.crashinvaders.vfx.effects.FilmGrainEffect;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.common.gamestate.GameplayContext;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.data.DataKeys;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.event.v1.TickEvents;
import com.ultreon.bubbles.event.v1.VfxEffectBuilder;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.time.Date;
import com.ultreon.commons.time.DateTime;
import com.ultreon.commons.time.Time;
import com.ultreon.data.types.MapType;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class BloodMoonGameplayEvent extends GameplayEvent {
    private static final UUID NOISE_EFFECT_ID = UUID.fromString("7d6dfafe-bbe6-4795-bc09-8c778af55115");
    private static final Color UPPER_COLOR = Color.rgb(0xff3000);
    private static final Color LOWER_COLOR = Color.CRIMSON;
    private final Date date = new Date(31, 10, 0);
    private final Time timeLo = new Time(3, 0, 0);
    private final Time timeHi = new Time(3, 59, 59);

    private boolean wasActive = false;
    private boolean wasPlayerActive = false;

    private final Map<Player, Double> playerDefenses = new ConcurrentHashMap<>();
    private boolean activating;
    private boolean deactivating;
    private long stopTime;

    public BloodMoonGameplayEvent() {
        super();

        TickEvents.TICK_GAME.listen(this::onUpdate);
    }

    public void onUpdate(BubbleBlaster game) {
        LoadedGame loadedGame = game.getLoadedGame();

        if (loadedGame == null) {
            return;
        }

        World world = loadedGame.getWorld();

        if (this.stopTime < System.currentTimeMillis()) {
            world.stopBloodMoon();
        }

        if (this.activating) {
            this.activating = false;
            this.deactivating = false;

            this.stopTime = System.currentTimeMillis() + 60000;

            // Game effects.
            if (!this.wasActive) BubbleBlaster.getLogger().info("Blood Moon activated!");

            world.triggerBloodMoon();
            world.setStateDifficultyModifier(this, 64f);
            this.wasActive = true;

            // Player effects.
            if (!this.wasPlayerActive && loadedGame.getGamemode().getPlayer() != null) {
                BubbleBlaster.getLogger().info("Blood Moon for player activated!");
                // Todo: implement this.
//                playerDefenses.put(GameScene.getGameType().getPlayer(), GameScene.getGameType().getPlayer().getDefenseModifier());
                this.wasPlayerActive = true;
            }
        } else if (this.deactivating) {
            this.deactivating = false;
            // Game effects.
            if (this.wasActive) {
                BubbleBlaster.getLogger().info("Blood Moon deactivated!");
                world.removeStateDifficultyModifier(this);
                this.wasActive = false;
            }
        }
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

        MapType storage = context.gameplayStorage().get(BubbleBlaster.NAMESPACE);
        return storage.getBoolean(DataKeys.BLOOD_MOON_ACTIVE);
    }

    @Override
    public final boolean shouldContinue(GameplayContext context) {
        if (!super.shouldContinue(context)) return false;

        MapType storage = context.gameplayStorage().get(BubbleBlaster.NAMESPACE);
        return storage.getBoolean(DataKeys.BLOOD_MOON_ACTIVE);
    }

    @Override
    public void renderBackground(World world, Renderer renderer) {
        BubbleBlaster instance = BubbleBlaster.getInstance();
        renderer.fillGradient(instance.getBounds(), UPPER_COLOR, LOWER_COLOR);
    }

    public final boolean wouldActive(DateTime dateTime) {
        boolean flag1 = dateTime.getTime().isBetween(this.timeLo, this.timeHi);  // Devil's hour.
        boolean flag2 = dateTime.getDate().equalsIgnoreYear(this.date);  // Halloween.

        boolean flag3 = dateTime.getDate().getDayOfWeek() == DayOfWeek.FRIDAY;  // Friday
        boolean flag4 = dateTime.getDate().getDay() == 13;  // 13th

        return (flag1 && flag2) || (flag3 && flag4);  // Every October 31st in devil's hour. Or Friday 13th.
    }

    public void deactivate() {
        this.deactivating = true;
    }

    public void activate() {
        this.activating = true;
    }

    public Map<Player, Double> getPlayerDefenses() {
        return this.playerDefenses;
    }
}
