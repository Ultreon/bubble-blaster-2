package dev.ultreon.bubbles.settings;

import com.badlogic.gdx.Input;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.common.Difficulty;
import dev.ultreon.bubbles.common.GameFolders;
import dev.ultreon.bubbles.event.v1.GameEvents;
import dev.ultreon.bubbles.gamemode.Gamemode;
import dev.ultreon.bubbles.init.Gamemodes;
import dev.ultreon.bubbles.input.Keybind;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.translations.v1.LanguageManager;

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
    @Deprecated
    private String language = "en";

    public Identifier gamemode = Gamemodes.NORMAL.id();
    public final GraphicsSettings graphicsSettings = new GraphicsSettings();
    public Difficulty difficulty = Difficulty.NORMAL;
    public final DebugOptions debugOptions = new DebugOptions();


    static {
        if (!GameSettings.reload()) {
            BubbleBlaster.getLogger().error("Failed to load settings.");
        }
    }

    private GameSettings() {

    }

    public synchronized static boolean reload() {
        var gson = new Gson();

        instance = new GameSettings();
        if (!GameFolders.SETTINGS_FILE.exists()) {
            return GameSettings.save();
        }

        try {
            var json = GameFolders.SETTINGS_FILE.readString();
            var instance = gson.fromJson(json, GameSettings.class);
            if (!Registries.GAMEMODES.contains(instance.gamemode)) {
                instance.gamemode = Gamemodes.NORMAL.id();
                GameSettings.save();
            }
            GameSettings.instance = instance;
        } catch (Exception e) {
            BubbleBlaster.getLogger().error("Failed to load settings from " + GameFolders.SETTINGS_FILE.path() + ":", e);
            return GameSettings.save();
        }

        return GameSettings.save();
    }

    public synchronized static boolean save() {
        var settingsFile = GameFolders.SETTINGS_FILE;

        var json = GSON.toJson(instance);
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

    @Deprecated(forRemoval = true)
    public String getLanguage() {
        return this.language;
    }

    @Deprecated(forRemoval = true)
    public Locale getLanguageLocale() {
        return new Locale(this.getLanguage());
    }

    @Deprecated(forRemoval = true)
    public void setLanguage(String language) {
        this.language = language;
        var oldLang = this.language;
        var oldLocale = Locale.forLanguageTag(oldLang);
        var newLocale = Locale.forLanguageTag(language);
        LanguageManager.setCurrentLanguage(newLocale);
        GameEvents.LANGUAGE_CHANGED.factory().onLanguageChanged(oldLocale, newLocale);
    }

    @Deprecated(forRemoval = true)
    public void setLanguage(Locale locale) {
        var oldLang = this.language;
        var oldLocale = Locale.forLanguageTag(oldLang);
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
