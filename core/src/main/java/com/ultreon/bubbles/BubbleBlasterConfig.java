package com.ultreon.bubbles;

import com.ultreon.bubbles.common.DifficultyEffectType;
import com.ultreon.bubbles.config.Config;
import com.ultreon.bubbles.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;

public class BubbleBlasterConfig {
    public static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bubble_blaster.toml");

    public static final Config.BooleanEntry ENABLE_ANNOYING_EASTER_EGGS;
    public static final Config.IntEntry AUTO_SAVE_RATE;
    public static final Config.IntEntry SECS_BEFORE_RED_EFFECT_TIME;
    public static final Config.IntEntry LEVEL_THRESHOLD;
    public static final Config.DoubleEntry BASE_BUBBLE_SPEED;
    public static final Config.FloatEntry BUBBLE_LINE_THICKNESS;
    public static final Config.IntEntry BOOST_COOLDOWN;
    public static final Config.IntEntry BOOST_DURATION;
    public static final Config.EnumEntry<DifficultyEffectType> DIFFICULTY_EFFECT_TYPE;

    private static final Config SPEC;

    static {
        Config.Builder builder = new Config.Builder(FILE);
        ENABLE_ANNOYING_EASTER_EGGS = builder.entry("generic.enableAnnoyingEasterEggs").comment("Enables easter eggs that can be annoying in some way.").value(false);
        AUTO_SAVE_RATE = builder.entry("generic.autoSaveRate").comment("The rate in seconds of which the game automatically saves.").withinRange(30, 3600, 60);
        SECS_BEFORE_RED_EFFECT_TIME = builder.entry("modernHud.secsBeforeRedEffectTime").comment("How many seconds left for the time of the status effect gets red.").withinRange(0, 20, 2);
        LEVEL_THRESHOLD = builder.entry("gameplay.levelThreshold").comment("How much score do you need to get a new level").withinRange(1_000, 100_000, 8_800);
        BASE_BUBBLE_SPEED = builder.entry("gameplay.baseBubbleSpeed").comment("Speed of bubbles at level 1. (Will still increase after each level)").withinRange(0.1, 50.0, 2.0);
        BOOST_COOLDOWN = builder.entry("gameplay.boostCooldown").comment("How much time to wait until boost refills. (In milliseconds)").withinRange(500, 240000, 120000);
        BOOST_DURATION = builder.entry("gameplay.boostDuration").comment("The amount of time to accelerate. (In milliseconds)").withinRange(500, 5000, 1000);
        BUBBLE_LINE_THICKNESS = builder.entry("graphical.bubbleLineThickness").comment("The thickness of a singular circle of a bubble.").withinRange(1f, 2.5f, 2f);
        DIFFICULTY_EFFECT_TYPE = builder.entry("gameplay.difficultyEffectType").comment("The type of difficulty effect.").value(DifficultyEffectType.LOCAL);

        SPEC = builder.build();
    }

    @ApiStatus.Internal
    public static void register() {
        ConfigManager.registerConfig(BubbleBlaster.NAMESPACE, SPEC);
    }
}
