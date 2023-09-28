package com.ultreon.bubbles;

import com.badlogic.gdx.math.GridPoint2;

import java.util.concurrent.TimeUnit;

public class Constants {
    public static final GridPoint2 DEFAULT_SIZE = new GridPoint2(1280, 720);
    public static final String DISCORD_GAME_SDK_VERSION = "3.2.1"; // Todo: check if this works, else use 2.5.6

    // Auto Save
    public static final long AUTO_SAVE_RATE = 30;
    public static final TimeUnit AUTO_SAVE_RATE_UNIT = TimeUnit.SECONDS;
}
