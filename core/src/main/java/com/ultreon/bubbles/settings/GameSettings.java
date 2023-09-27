package com.ultreon.bubbles.settings;

import com.badlogic.gdx.Input;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.common.References;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.input.Keybind;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.translations.v0.LanguageManager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

@SuppressWarnings("FieldMayBeFinal")
public final class GameSettings implements Serializable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .create();
    private static GameSettings instance;
    public static Keybind keybindForward = new Keybind(Input.Keys.UP);

    public static Keybind keybindBackward = new Keybind(Input.Keys.DOWN);

    public static Keybind keybindRotateLeft = new Keybind(Input.Keys.LEFT);
    public static Keybind keybindRotateRight = new Keybind(Input.Keys.RIGHT);
    @SerializedName("max-bubbles")
    private int maxBubbles = 200;
    @SerializedName("lang")
    private String language = new Locale("en").getLanguage();

    @SerializedName("gamemode")
    private Identifier gamemode = Gamemodes.MODERN.id();
    @SerializedName("graphics")
    private GraphicsSettings graphicsSettings = new GraphicsSettings();
    @SerializedName("difficulty")
    private Difficulty difficulty = Difficulty.NORMAL;
    @SerializedName("debug")
    private DebugOptions debugOptions = new DebugOptions();


    static {
        if (!reload()) {
            BubbleBlaster.getLogger().error("Failed to load settings.");
        }

        BubbleBlaster.getWatcher().watchFile(References.SETTINGS_FILE, file -> reload());
    }

    private GameSettings() {
        LanguageManager.setCurrentLanguage(Locale.forLanguageTag(language));
    }

    public synchronized static boolean reload() {
        Gson gson = new Gson();

        instance = new GameSettings();
        if (!References.SETTINGS_FILE.exists()) {
            return save();
        }

        try {
            String json = Files.readString(References.SETTINGS_FILE.toPath());
            var instance = gson.fromJson(json, GameSettings.class);
            if (!Registries.GAMEMODES.contains(instance.gamemode)) {
                instance.gamemode = Gamemodes.MODERN.id();
            }
            GameSettings.instance = instance;
            LanguageManager.setCurrentLanguage(instance.getLanguageLocale());
        } catch (Exception e) {
            BubbleBlaster.getLogger().error("Failed to load settings from " + References.SETTINGS_FILE.toPath() + ":", e);
            return save();
        }

        return save();
    }

    public synchronized static boolean save() {
        File settingsFile = References.SETTINGS_FILE;

        String json = GSON.toJson(instance);
        try {
            Files.writeString(settingsFile.toPath(), json, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            BubbleBlaster.getLogger().error("Failed to save settings to " + settingsFile + ":", e);
            return false;
        }
        return true;
    }

    public static GameSettings instance() {
        return instance;
    }

    public int getMaxBubbles() {
        return maxBubbles;
    }

    public void setMaxBubbles(int value) {
        this.maxBubbles = value;
        save();
    }

    public String getLanguage() {
        return language;
    }

    public Locale getLanguageLocale() {
        return new Locale(getLanguage());
    }

    public void setLanguage(String language) {
        this.language = language;
        String oldLang = this.language;
        Locale oldLocale = Locale.forLanguageTag(oldLang);
        Locale newLocale = Locale.forLanguageTag(language);
        LanguageManager.setCurrentLanguage(newLocale);
        GameEvents.LANGUAGE_CHANGED.factory().onLanguageChanged(oldLocale, newLocale);

        save();
    }

    public void setLanguage(Locale locale) {
        String oldLang = this.language;
        Locale oldLocale = Locale.forLanguageTag(oldLang);
        this.language = locale.toLanguageTag();
        LanguageManager.setCurrentLanguage(locale);
        GameEvents.LANGUAGE_CHANGED.factory().onLanguageChanged(oldLocale, locale);
        save();
    }

    public GraphicsSettings getGraphicsSettings() {
        return graphicsSettings;
    }

    public Gamemode getGamemode() {
        return Registries.GAMEMODES.getValue(gamemode);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public DebugOptions getDebugOptions() {
        return debugOptions;
    }

    public static void nopInit() {

    }
}
