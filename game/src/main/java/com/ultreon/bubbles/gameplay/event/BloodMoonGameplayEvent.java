package com.ultreon.bubbles.gameplay.event;

import com.jhlabs.image.NoiseFilter;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v1.FilterBuilder;
import com.ultreon.bubbles.event.v1.TickEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.LoadedGame;
import com.ultreon.bubbles.render.Color;
import com.ultreon.commons.time.Date;
import com.ultreon.commons.time.DateTime;
import com.ultreon.commons.time.Time;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class BloodMoonGameplayEvent extends GameplayEvent {
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

        setBackgroundColor(Color.hex("#af0000"));

        TickEvents.TICK_GAME.listen(this::onUpdate);
    }

    public void onUpdate(BubbleBlaster game) {
        LoadedGame loadedGame = game.getLoadedGame();

        if (loadedGame == null) {
            return;
        }

        Environment environment = loadedGame.getEnvironment();

        if (stopTime < System.currentTimeMillis()) {
            environment.stopBloodMoon();
        }

        if (activating) {
            activating = false;
            deactivating = false;

            stopTime = System.currentTimeMillis() + 60000;

            // Game effects.
            if (!wasActive) BubbleBlaster.getLogger().info("Blood Moon activated!");

            environment.triggerBloodMoon();
            environment.setStateDifficultyModifier(this, 64f);
            wasActive = true;

            // Player effects.
            if (!wasPlayerActive && loadedGame.getGamemode().getPlayer() != null) {
                BubbleBlaster.getLogger().info("Blood Moon for player activated!");
                // Todo: implement this.
//                playerDefenses.put(GameScene.getGameType().getPlayer(), GameScene.getGameType().getPlayer().getDefenseModifier());
                wasPlayerActive = true;
            }
        } else if (deactivating) {
            deactivating = false;
            // Game effects.
            if (wasActive) {
                BubbleBlaster.getLogger().info("Blood Moon deactivated!");
                environment.removeStateDifficultyModifier(this);
                wasActive = false;
            }
        }
    }

    public void onFilter(FilterBuilder builder) {
        if (!isActive(DateTime.current())) return;

        NoiseFilter filter = new NoiseFilter();

        filter.setMonochrome(true);
        filter.setDensity(0.25f);
        filter.setAmount(60);
        filter.setDistribution(1);

        builder.addFilter(filter);
    }

    @Override
    public final boolean isActive(DateTime dateTime) {
        super.isActive(dateTime);

        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        if (loadedGame == null) {
            return false;
        }

        return loadedGame.getEnvironment().isBloodMoonActive();
    }

    public final boolean wouldActive(DateTime dateTime) {
        boolean flag1 = dateTime.getTime().isBetween(timeLo, timeHi);  // Devil's hour.
        boolean flag2 = dateTime.getDate().equalsIgnoreYear(date);  // Halloween.

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
        return playerDefenses;
    }
}
