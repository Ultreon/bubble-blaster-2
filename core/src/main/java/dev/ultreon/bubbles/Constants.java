package dev.ultreon.bubbles;

import com.badlogic.gdx.math.GridPoint2;
import dev.ultreon.bubbles.common.DifficultyEffectType;

import java.util.concurrent.TimeUnit;

public class Constants {
    public static final GridPoint2 DEFAULT_SIZE = new GridPoint2(1280, 720);
    public static final String DISCORD_GAME_SDK_VERSION = "3.2.1";

    // Auto Save
    public static final long AUTO_SAVE_RATE = 30;
    public static final TimeUnit AUTO_SAVE_RATE_UNIT = TimeUnit.SECONDS;
    public static final int RENDER_SAMPLES = 16;
    public static final double BASE_BUBBLE_SPEED = 2.0;
    public static final DifficultyEffectType DIFFICULTY_EFFECT_TYPE = DifficultyEffectType.LOCAL;
    public static final int BOOST_COOLDOWN = 120_000;
    public static final int LEVEL_THRESHOLD = 1_000;
    public static final double BUBBLE_SCORE_REDUCTION_SELF = 16.0;
    public static final double BUBBLE_SCORE_REDUCTION = 16.0;
    public static final int SHOOT_COOLDOWN = 5000;
    public static final int BOOST_DURATION = 300;
    public static final int TIME_LIMIT = 60;
}
