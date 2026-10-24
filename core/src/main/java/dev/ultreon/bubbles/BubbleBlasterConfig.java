package dev.ultreon.bubbles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import dev.ultreon.bubbles.common.DifficultyEffectType;
import dev.ultreon.bubbles.config.Config;
import dev.ultreon.bubbles.config.ConfigManager;
import dev.ultreon.bubbles.event.v1.ConfigEvents;
import dev.ultreon.bubbles.init.HudTypes;
import dev.ultreon.bubbles.notification.Notification;
import dev.ultreon.bubbles.render.gui.screen.LoadScreen;
import dev.ultreon.bubbles.lang.LanguageManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class BubbleBlasterConfig {
    public static final FileHandle FILE = BubbleBlaster.getConfigDir().child("bubble_blaster.toml");

    // Generic
    public static final Config.BooleanEntry ENABLE_ANNOYING_EASTER_EGGS;
    public static final Config.BooleanEntry ENABLE_EASTER_EGGS;
    public static final Config.IntEntry AUTO_SAVE_RATE;
    public static final Config.IntEntry MAX_FRAMERATE;
    public static final Config.BooleanEntry FULLSCREEN;
    public static final Config.StringEntry LANGUAGE;
    public static final Config.BooleanEntry ENABLE_TOUCH_PRESSURE;

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
    public static final Config.IntEntry BLOOD_MOON_TRIGGER_LOW;
    public static final Config.IntEntry BLOOD_MOON_TRIGGER_HIGH;

    static final Config CONFIG;



    static {
        var builder = new Config.Builder(FILE.file());

        // Generic
        ENABLE_ANNOYING_EASTER_EGGS = builder.entry("enableAnnoyingEasterEggs").comment("Enables easter eggs that mess with gameplay a little to hard.").value(false);
        ENABLE_EASTER_EGGS = builder.entry("enableEasterEggs").comment("Enables hidden easter eggs.").value(true);
        AUTO_SAVE_RATE = builder.entry("autoSaveRate").comment("The rate in seconds of which the game automatically saves.").withinRange(30, 3600, 60);
        MAX_FRAMERATE = builder.entry("maxFramerate").comment("Maximum framerate limit.").withinRange(10, 240, GamePlatform.get().getRecommendedFPS());
        FULLSCREEN = builder.entry("fullscreen").comment("Play the game in fullscreen mode.").value(false);
        LANGUAGE = builder.entry("language").comment("The language of the game.").value(new Locale("en", "us").toLanguageTag());

        // Gameplay
        ENABLE_TOUCH_PRESSURE = builder.entry("gameplay.touchPressure").comment("Enable touch sensitivity to control speed of the player.").value(false);
        BLOOD_MOON_STOP_LOW = builder.entry("gameplay.bloodMoon.deactivateLow").comment("The lower point of deactivation time (in seconds) for the blood moon event. (Random between lower and higher)").withinRange(10, 60, 10);
        BLOOD_MOON_STOP_HIGH = builder.entry("gameplay.bloodMoon.deactivateHigh").comment("The higher point of deactivation time (in seconds) for the blood moon event. (Random between lower and higher)").withinRange(10, 60, 25);
        BLOOD_MOON_TRIGGER_LOW = builder.entry("gameplay.bloodMoon.triggerLow").comment("The lower point of trigger time (in seconds) between blood moon events. (Random between lower and higher)").withinRange(30, 900, 240);
        BLOOD_MOON_TRIGGER_HIGH = builder.entry("gameplay.bloodMoon.triggerHigh").comment("The higher point of trigger time (in seconds) between blood moon events. (Random between lower and higher)").withinRange(30, 900, 540);

        // Graphical
        SECS_BEFORE_RED_EFFECT_TIME = builder.entry("graphical.secsBeforeRedEffectTime").comment("How many seconds left for the time of the status effect gets red.").withinRange(0, 20, 2);
        BUBBLE_LINE_THICKNESS = builder.entry("graphical.bubbleLineThickness").comment("The thickness of a singular circle of a bubble.").withinRange(1f, 5f, 3f);
        DEFAULT_EFFECT_SPEED = builder.entry("graphical.defaultEffectSpeed").comment("How long it takes for one cycle of the scrolling gradient effect by default.").withinRange(0f, 30f, 10f);
        GAME_HUD = builder.entry("graphical.gameHud").comment("Which game hud to use for playing.").value(Objects.requireNonNull(HudTypes.MODERN.id()).toString());

        // Debug
        DEBUG_DISABLE_SCISSORS = builder.entry("debug.disableScissors").comment("Disables ScissorStack.pushScissors() and ScissorStack.popScissors()").value(false);
        DEBUG_LOG_EMPTY_SCISSORS = builder.entry("debug.logEmptyScissors").comment("Logs if a scissor call results into an empty glScissor() call.").value(false);
        DEBUG_LOG_SCREENS = builder.entry("debug.logScreens").comment("Logs when a new screen has opened.").value(false);

        CONFIG = builder.build();

        ConfigEvents.CONFIG_RELOADED.listen(reloaded -> {
            if (reloaded.equals(CONFIG)) {
                BubbleBlasterConfig.onReload();
            }
        });
    }

    @ApiStatus.Internal
    public static void register() {
        ConfigManager.registerConfig(BubbleBlaster.NAMESPACE, CONFIG);
    }

    public static void onReload() {
        BubbleBlaster.LOGGER.debug("Post-reloading config!");
        BubbleBlasterConfig.reloadLanguage();
    }

    private static void reloadLanguage() {
        var languageTag = LANGUAGE.get();
        var locale = Locale.forLanguageTag(languageTag);

        if (!LoadScreen.isLanguagesLoaded()) return;

        if (locale == null || !LanguageManager.INSTANCE.getLocales().contains(locale)) {
            BubbleBlaster.LOGGER.warn("Invalid language: {}", languageTag);
            BubbleBlaster.LOGGER.warn("Those are valid: {}", LanguageManager.INSTANCE.getLocales().stream().map(Locale::toLanguageTag).collect(Collectors.joining(", ")));
            BubbleBlaster.getInstance().notifications.notify(Notification.builder("Invalid Language!", "Language Tag: " + languageTag).subText("Language Manager").build());
            LANGUAGE.reset();
        } else {
            BubbleBlaster.LOGGER.debug("Setting language to {}", locale.toLanguageTag());
            LanguageManager.setCurrentLanguage(locale);
        }
    }

    public static void save() {
        CONFIG.save();

        BubbleBlaster.invoke(() -> {
            if (MAX_FRAMERATE.get() >= 240) {
                Gdx.graphics.setForegroundFPS(0);
            } else {
                Gdx.graphics.setForegroundFPS(MAX_FRAMERATE.get());
            }
        });
    }

    public static void reload() {
        CONFIG.reload();
    }
}
