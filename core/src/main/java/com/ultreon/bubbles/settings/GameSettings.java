package com.ultreon.bubbles.settings;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.common.GameFolders;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.input.Keybind;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.translations.v1.LanguageManager;

import java.io.Serializable;
import java.util.Locale;

public final class GameSettings implements Serializable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .create();
    private static GameSettings instance;
    public Keybind keybindForward = new Keybind(Input.Keys.UP);

    public Keybind keybindBackward = new Keybind(Input.Keys.DOWN);

    public Keybind keybindRotateLeft = new Keybind(Input.Keys.LEFT);
    public Keybind keybindRotateRight = new Keybind(Input.Keys.RIGHT);
    public int maxBubbles = 500;
    private String language = "en";

    public Identifier gamemode = Gamemodes.NORMAL.id();
    public final GraphicsSettings graphicsSettings = new GraphicsSettings();
    public Difficulty difficulty = Difficulty.NORMAL;
    public final DebugOptions debugOptions = new DebugOptions();


    static {
        if (!GameSettings.reload()) {
            BubbleBlaster.getLogger().error("Failed to load settings.");
        }

//        BubbleBlaster.getWatcher().watchFile(GameFolders.SETTINGS_FILE.file(), file -> GameSettings.reload());
    }

    private GameSettings() {
        LanguageManager.setCurrentLanguage(Locale.forLanguageTag(this.language));
    }

    public synchronized static boolean reload() {
        Gson gson = new Gson();

        instance = new GameSettings();
        if (!GameFolders.SETTINGS_FILE.exists()) {
            return GameSettings.save();
        }

        try {
            String json = GameFolders.SETTINGS_FILE.readString();
            GameSettings instance = gson.fromJson(json, GameSettings.class);
            if (!Registries.GAMEMODES.contains(instance.gamemode)) {
                instance.gamemode = Gamemodes.NORMAL.id();
                GameSettings.save();
            }
            GameSettings.instance = instance;
            LanguageManager.setCurrentLanguage(instance.getLanguageLocale());
        } catch (Exception e) {
            BubbleBlaster.getLogger().error("Failed to load settings from " + GameFolders.SETTINGS_FILE.path() + ":", e);
            return GameSettings.save();
        }

        return GameSettings.save();
    }

    public synchronized static boolean save() {
        FileHandle settingsFile = GameFolders.SETTINGS_FILE;

        String json = GSON.toJson(instance);
        try {
            settingsFile.writeString(json, false, "UTF-8");
        } catch (Exception e) {
            BubbleBlaster.getLogger().error("Failed to save settings to " + settingsFile + ":", e);
            return false;
        }
        return true;
    }

    public static GameSettings instance() {
        return instance;
    }

    public String getLanguage() {
        return this.language;
    }

    public Locale getLanguageLocale() {
        return new Locale(this.getLanguage());
    }

    public void setLanguage(String language) {
        this.language = language;
        String oldLang = this.language;
        Locale oldLocale = Locale.forLanguageTag(oldLang);
        Locale newLocale = Locale.forLanguageTag(language);
        LanguageManager.setCurrentLanguage(newLocale);
        GameEvents.LANGUAGE_CHANGED.factory().onLanguageChanged(oldLocale, newLocale);
    }

    public void setLanguage(Locale locale) {
        String oldLang = this.language;
        Locale oldLocale = Locale.forLanguageTag(oldLang);
        this.language = locale.toLanguageTag();
        LanguageManager.setCurrentLanguage(locale);
        GameEvents.LANGUAGE_CHANGED.factory().onLanguageChanged(oldLocale, locale);
    }

    public GraphicsSettings getGraphicsSettings() {
        return this.graphicsSettings;
    }

    public Gamemode getGamemode() {
        return Registries.GAMEMODES.getValue(this.gamemode);
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public DebugOptions getDebugOptions() {
        return this.debugOptions;
    }

    public static void nopInit() {

    }
}
