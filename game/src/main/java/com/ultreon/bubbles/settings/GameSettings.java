package com.ultreon.bubbles.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.References;
import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.input.Keybind;
import com.ultreon.bubbles.registry.Registry;

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
//            .registerTypeHierarchyAdapter(Identifier.class, new IdentifierAdapter())
            .create();
    private static GameSettings instance;
    public static Keybind keybindForward = new Keybind(KeyboardInput.Map.KEY_UP);

    public static Keybind keybindBackward = new Keybind(KeyboardInput.Map.KEY_DOWN);

    public static Keybind keybindRotateLeft = new Keybind(KeyboardInput.Map.KEY_LEFT);
    public static Keybind keybindRotateRight = new Keybind(KeyboardInput.Map.KEY_RIGHT);
    @SerializedName("max-bubbles")
    private int maxBubbles = 200;
    @SerializedName("lang")
    private String language = "en_US";

    @SerializedName("gamemode")
    private Identifier gamemode = Gamemodes.CLASSIC.id();
    @SerializedName("graphics")
    private GraphicsSettings graphicsSettings = new GraphicsSettings();
    @SerializedName("difficulty")
    private Difficulty difficulty = Difficulty.NORMAL;

    static {
        if (!reload()) {
            BubbleBlaster.getLogger().error("Failed to load settings.");
        }

        BubbleBlaster.getWatcher().watchFile(References.SETTINGS_FILE, file -> reload());
    }

    public GameSettings() {

    }

    public synchronized static boolean reload() {
        Gson gson = new Gson();

        instance = new GameSettings();
        if (!References.SETTINGS_FILE.exists()) {
            return save();
        }

        BubbleBlaster.getLogger().info("Loading settings...");

        try {
            String json = Files.readString(References.SETTINGS_FILE.toPath());
            var instance = gson.fromJson(json, GameSettings.class);
            if (!Registry.GAMEMODES.contains(instance.gamemode)) {
                instance.gamemode = Gamemodes.CLASSIC.id();
            }
            GameSettings.instance = instance;
            BubbleBlaster.getLogger().info("Loaded settings");
        } catch (Exception e) {
            BubbleBlaster.getLogger().error("Failed to load settings from: " + References.SETTINGS_FILE.toPath());
            e.printStackTrace();
            return save();
        }

        return save();
    }

    public synchronized static boolean save() {
        File settingsFile = References.SETTINGS_FILE;

        BubbleBlaster.getLogger().info("Saving settings...");

        String json = GSON.toJson(instance);
        try {
            Files.writeString(settingsFile.toPath(), json, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            BubbleBlaster.getLogger().error("Failed to save settings to: " + settingsFile);
            e.printStackTrace();
            return false;
        }

        BubbleBlaster.getLogger().info("Settings saved!");
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
        save();
    }

    public void setLanguage(Locale language) {
        this.language = language.toString();
        save();
    }

    public GraphicsSettings getGraphicsSettings() {
        return graphicsSettings;
    }

    public Gamemode getGamemode() {
        return Registry.GAMEMODES.getValue(gamemode);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
