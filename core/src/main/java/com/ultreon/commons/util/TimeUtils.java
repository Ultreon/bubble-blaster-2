package com.ultreon.commons.util;

import com.ultreon.libs.commons.v0.UtilityClass;
import com.ultreon.libs.datetime.v0.Duration;
import org.jetbrains.annotations.NotNull;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

public class TimeUtils extends UtilityClass {
    private static final long MSPT = 1000 / TPS;

    private TimeUtils() {
        super();
    }

    public static Duration ofTicks(long ticks) {
        return Duration.ofMilliseconds(ticks * MSPT);
    }

    public static long toTicks(Duration duration) {
        return duration.toMillis() / MSPT;
    }

    /**
     * @param seconds the desired seconds to format.
     * @return the formatted seconds.
     */
    @Deprecated(forRemoval = true)
    public static String formatDuration(long seconds) {
        return Duration.ofSeconds(seconds).toSimpleString();
    }

    /**
     * @param duration the desired duration to format.
     * @return the formatted duration.
     * @deprecated use {@link Duration#toSimpleString()}
     */
    @SuppressWarnings("UnstableApiUsage")
    @Deprecated(forRemoval = true)
    public static String formatDuration(Duration duration) {
        return duration.toSimpleString();
    }

    @NotNull
    @Deprecated(forRemoval = true)
    public static String formatDuration(int minute, int second, double hourDouble) {
        hourDouble -= (double) minute / 60;
        hourDouble -= (double) second / 60 / 60;

        int hour = (int) hourDouble;

        String minuteString = Integer.toString(minute);
        String secondString = Integer.toString(second);

        if (minuteString.length() == 1) minuteString = "0" + minuteString;
        if (secondString.length() == 1) secondString = "0" + secondString;

        return hour + ":" + minuteString + ":" + secondString;
    }
}
