package com.ultreon.bubbles.common;

import com.ultreon.bubbles.game.BubbleBlaster;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

public class References {
    // Dirs
    public static final File GAME_DIR = BubbleBlaster.getGameDir();
    public static final File LOGS_DIR = new File(GAME_DIR.getAbsolutePath(), "Logs");
    public static final File MODS_DIR = new File(GAME_DIR.getAbsolutePath(), "Mods");
    public static final File SAVES_DIR = new File(GAME_DIR, "Saves");

    // Files
    public static final File SETTINGS_FILE = new File(GAME_DIR, "Settings.json");
    public static final File CRASH_REPORTS = new File(GAME_DIR, "Game-Crashes");

    static {
        if (!LOGS_DIR.exists() && !LOGS_DIR.mkdirs()) {
            throw new IOError(new IOException("Couldn't make directories. " + LOGS_DIR.getAbsolutePath()));
        }
    }
}
