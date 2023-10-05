package com.ultreon.bubbles.common;

import com.badlogic.gdx.files.FileHandle;
import com.ultreon.bubbles.BubbleBlaster;

public class GameFolders {
    // Dirs
    public static final FileHandle DATA_DIR = BubbleBlaster.getDataDir();
    public static final FileHandle LOGS_DIR = DATA_DIR.child("logs");
    public static final FileHandle SAVES_DIR = DATA_DIR.child("saves");
    public static final FileHandle CONFIG_DIR = DATA_DIR.child("config");

    // Files
    public static final FileHandle SETTINGS_FILE = DATA_DIR.child("settings.json");
    public static final FileHandle CRASH_REPORTS = DATA_DIR.child("game-crashes");

    static {
        if (!LOGS_DIR.exists()) {
            LOGS_DIR.mkdirs();
        }
    }
}
