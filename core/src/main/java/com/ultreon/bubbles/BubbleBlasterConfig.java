package com.ultreon.bubbles;

import com.badlogic.gdx.Gdx;
import com.ultreon.bubbles.common.DifficultyEffectType;
import com.ultreon.bubbles.config.Config;
import com.ultreon.bubbles.config.ConfigManager;
import com.ultreon.bubbles.event.v1.ConfigEvents;
import com.ultreon.bubbles.init.HudTypes;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.gui.hud.HudType;
import com.ultreon.libs.commons.v0.Identifier;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.util.Objects;

public class BubbleBlasterConfig {
    public static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bubble_blaster.toml");

    // Generic
    public static final Config.BooleanEntry ENABLE_ANNOYING_EASTER_EGGS;
    public static final Config.IntEntry AUTO_SAVE_RATE;
    public static final Config.IntEntry MAX_FRAMERATE;

    // Gameplay
    public static final Config.IntEntry LEVEL_THRESHOLD;
    public static final Config.DoubleEntry BASE_BUBBLE_SPEED;
    public static final Config.IntEntry SHOOT_COOLDOWN;
    public static final Config.IntEntry BOOST_COOLDOWN;
    public static final Config.IntEntry BOOST_DURATION;
    public static final Config.DoubleEntry BUBBLE_SCORE_REDUCTION;
    public static final Config.DoubleEntry BUBBLE_SCORE_REDUCTION_SELF;
    public static final Config.EnumEntry<DifficultyEffectType> DIFFICULTY_EFFECT_TYPE;

    // Graphical
    public static final Config.IntEntry SECS_BEFORE_RED_EFFECT_TIME;
    public static final Config.FloatEntry BUBBLE_LINE_THICKNESS;
    public static final Config.FloatEntry DEFAULT_EFFECT_SPEED;
    public static final Config.StringEntry GAME_HUD;

    // Debug
    public static final Config.BooleanEntry DEBUG_DISABLE_SCISSORS;
    public static final Config.BooleanEntry DEBUG_LOG_EMPTY_SCISSORS;
    public static final Config.BooleanEntry DEBUG_LOG_SCREENS;
    public static final Config.IntEntry BLOOD_MOON_STOP_LOW;
    public static final Config.IntEntry BLOOD_MOON_STOP_HIGH;
    public static final Config.BooleanEntry FULLSCREEN;

    static final Config CONFIG;

    static {
        Config.Builder builder = new Config.Builder(FILE);

        // Generic
        ENABLE_ANNOYING_EASTER_EGGS = builder.entry("generic.enableAnnoyingEasterEggs").comment("Enables easter eggs that can be annoying in some way.").value(false);
        AUTO_SAVE_RATE = builder.entry("generic.autoSaveRate").comment("The rate in seconds of which the game automatically saves.").withinRange(30, 3600, 60);
        MAX_FRAMERATE = builder.entry("generic.maxFramerate").comment("Maximum framerate limit.").withinRange(10, 240, 120);
        FULLSCREEN = builder.entry("generic.fullscreen").comment("Play the game in fullscreen mode.").value(false);

        // Gameplay
        LEVEL_THRESHOLD = builder.entry("gameplay.levelThreshold").comment("How much score do you need to get a new level").withinRange(1_000, 100_000, 10_000);
        BASE_BUBBLE_SPEED = builder.entry("gameplay.baseBubbleSpeed").comment("Speed of bubbles at level 1. (Will still increase after each level)").withinRange(0.1, 50.0, 2.0);
        SHOOT_COOLDOWN = builder.entry("gameplay.shootCooldown").comment("How much time to wait until the player can shoot a bullet again. (In milliseconds)").withinRange(50, 2_000, 500);
        BOOST_COOLDOWN = builder.entry("gameplay.boostCooldown").comment("How much time to wait until boost refills. (In milliseconds)").withinRange(500, 240_000, 120_000);
        BOOST_DURATION = builder.entry("gameplay.boostDuration").comment("The amount of time to accelerate. (In milliseconds)").withinRange(500, 5_000, 1_000);
        BUBBLE_SCORE_REDUCTION = builder.entry("gameplay.bubbleScoreReduction").comment("How much to reduce the score when using bullets.").withinRange(0.001, 0.04, 16.0);
        BUBBLE_SCORE_REDUCTION_SELF = builder.entry("gameplay.bubbleScoreReductionSelf").comment("How much to reduce the score when destroying bubbles using the ship.").withinRange(0.001, 0.1, 16.0);
        DIFFICULTY_EFFECT_TYPE = builder.entry("gameplay.difficultyEffectType").comment("The type of difficulty effect.").value(DifficultyEffectType.LOCAL);
        BLOOD_MOON_STOP_LOW = builder.entry("gameplay.bloodMoon.deactivateLow").comment("The lower point of deactivation time for the blood moon event. (Random between lower and higher)").withinRange(10, 60, 10);
        BLOOD_MOON_STOP_HIGH = builder.entry("gameplay.bloodMoon.deactivateHigh").comment("The higher point of deactivation time for the blood moon event. (Random between lower and higher)").withinRange(10, 60, 25);

        // Graphical
        SECS_BEFORE_RED_EFFECT_TIME = builder.entry("graphical.secsBeforeRedEffectTime").comment("How many seconds left for the time of the status effect gets red.").withinRange(0, 20, 2);
        BUBBLE_LINE_THICKNESS = builder.entry("graphical.bubbleLineThickness").comment("The thickness of a singular circle of a bubble.").withinRange(1f, 2.5f, 2f);
        DEFAULT_EFFECT_SPEED = builder.entry("graphical.defaultEffectSpeed").comment("How long it takes for one cycle of the scrolling gradient effect by default.").withinRange(0f, 30f, 10f);
        GAME_HUD = builder.entry("graphical.gameHud").comment("Which game hud to use for playing.").value(Objects.requireNonNull(HudTypes.MODERN.id()).toString());

        // Debug
        DEBUG_DISABLE_SCISSORS = builder.entry("debug.disableScissors").comment("Disables ScissorStack.pushScissors() and ScissorStack.popScissors()").value(false);
        DEBUG_LOG_EMPTY_SCISSORS = builder.entry("debug.logEmptyScissors").comment("Logs if a scissor call results into an empty glScissor() call.").value(false);
        DEBUG_LOG_SCREENS = builder.entry("debug.logScreens").comment("Logs when a new screen has opened.").value(false);

        CONFIG = builder.build();

        ConfigEvents.CONFIG_RELOADED.listen(reloaded -> {
            if (reloaded == CONFIG) {
                BubbleBlasterConfig.onReload();
            }
        });
    }

    @ApiStatus.Internal
    public static void register() {
        ConfigManager.registerConfig(BubbleBlaster.NAMESPACE, CONFIG);
    }

    public static void onReload() {
        int fps = MAX_FRAMERATE.get();
        Gdx.graphics.setForegroundFPS(fps == 240 ? 0 : fps);

        try {
            Identifier hudId = Identifier.tryParse(GAME_HUD.getOrDefault());
            if (hudId != null) {
                HudType hud = Registries.HUD.getValue(hudId);
                HudType.setCurrent(hud);
            }
        } catch (RuntimeException ignored) {

        }
    }

    public static void save() {
        CONFIG.save();
    }
}
