package com.ultreon.bubbles;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public class Constants {
    public static final double BUBBLE_SCORE_REDUCTION = 12.0;
    public static final double BUBBLE_SCORE_REDUCTION_SELF = 8.0;
    public static final int LEVEL_THRESHOLD = 8_000;
    public static final float BUBBLE_SPEED_MODIFIER = 2.0f;
    public static final boolean ALLOW_FULLSCREEN = false;
    public static final GridPoint2 DEFAULT_SIZE = new GridPoint2(1280, 720);
    public static final String DISCORD_GAME_SDK_VERSION = "3.2.1"; // Todo: check if this works, else use 2.5.6
}
