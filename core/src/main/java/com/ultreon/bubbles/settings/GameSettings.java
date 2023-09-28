package com.ultreon.bubbles.settings;

import com.badlogic.gdx.Input;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.common.GameFolders;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.input.Keybind;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.translations.v0.Language;
import com.ultreon.libs.translations.v0.LanguageManager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SuppressWarnings("FieldMayBeFinal")
public final class GameSettings implements Serializable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .create();
    private static GameSettings instance;
    public Keybind keybindForward = new Keybind(Input.Keys.UP);

    public Keybind keybindBackward = new Keybind(Input.Keys.DOWN);

    public Keybind keybindRotateLeft = new Keybind(Input.Keys.LEFT);
    public Keybind keybindRotateRight = new Keybind(Input.Keys.RIGHT);
    public int maxBubbles = 200;
    private String language = "en";

    public Identifier gamemode = Gamemodes.MODERN.id();
    public GraphicsSettings graphicsSettings = new GraphicsSettings();
    public Difficulty difficulty = Difficulty.NORMAL;
    public DebugOptions debugOptions = new DebugOptions();


    static {
        if (!reload()) {
            BubbleBlaster.getLogger().error("Failed to load settings.");
        }

        BubbleBlaster.getWatcher().watchFile(GameFolders.SETTINGS_FILE, file -> reload());
    }

    private GameSettings() {
        LanguageManager.setCurrentLanguage(Locale.forLanguageTag(language));
        List<Language> languages = LanguageManager.INSTANCE.getLanguages();
        if (languages.stream().noneMatch(language1 -> {
            System.out.println("language1.getLocale().toLanguageTag() = " + language1.getLocale().toLanguageTag());
            return language1.getLocale().equals(Locale.forLanguageTag(language));
        })) {
            BubbleBlaster.whenLoaded(UUID.fromString("fa7d8d9a-f707-4e83-8228-7af3af478857"), () -> {
                BubbleBlaster game = BubbleBlaster.getInstance();
                game.notifications.notifyOnce(
                    UUID.fromString("f904e6ae-9dc1-4534-8bf8-c87fc58d6182"),
                    new Notification("Error!", "Language not found: " + language, "Language Manager", Duration.ofSeconds(10))
                );
            });
        }
    }

    public synchronized static boolean reload() {
        Gson gson = new Gson();

        instance = new GameSettings();
        if (!GameFolders.SETTINGS_FILE.exists()) {
            return save();
        }

        try {
            String json = Files.readString(GameFolders.SETTINGS_FILE.toPath());
            var instance = gson.fromJson(json, GameSettings.class);
            if (!Registries.GAMEMODES.contains(instance.gamemode)) {
                instance.gamemode = Gamemodes.MODERN.id();
                GameSettings.save();
            }
            GameSettings.instance = instance;
            LanguageManager.setCurrentLanguage(instance.getLanguageLocale());
        } catch (Exception e) {
            BubbleBlaster.getLogger().error("Failed to load settings from " + GameFolders.SETTINGS_FILE.toPath() + ":", e);
            return save();
        }

        return save();
    }

    public synchronized static boolean save() {
        File settingsFile = GameFolders.SETTINGS_FILE;

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
    }

    public void setLanguage(Locale locale) {
        String oldLang = this.language;
        Locale oldLocale = Locale.forLanguageTag(oldLang);
        this.language = locale.toLanguageTag();
        LanguageManager.setCurrentLanguage(locale);
        GameEvents.LANGUAGE_CHANGED.factory().onLanguageChanged(oldLocale, locale);
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
