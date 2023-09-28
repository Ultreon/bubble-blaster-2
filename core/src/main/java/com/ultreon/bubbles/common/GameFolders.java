package com.ultreon.bubbles.common;

import com.ultreon.bubbles.BubbleBlaster;

import java.io.File;
import java.io.IOException;

public class GameFolders {
    // Dirs
    public static final File DATA_DIR = BubbleBlaster.getDataDir();
    public static final File LOGS_DIR = new File(DATA_DIR, "logs");
    public static final File SAVES_DIR = new File(DATA_DIR, "saves");

    // Files
    public static final File SETTINGS_FILE = new File(DATA_DIR, "settings.json");
    public static final File CRASH_REPORTS = new File(DATA_DIR, "game-crashes");

    static {
        if (!LOGS_DIR.exists() && !LOGS_DIR.mkdirs()) {
            throw new RuntimeException(new IOException("Couldn't make directories. " + LOGS_DIR.getAbsolutePath()));
        }
    }
}
