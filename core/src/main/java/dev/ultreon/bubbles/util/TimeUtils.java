package dev.ultreon.bubbles.util;

import dev.ultreon.libs.commons.v0.UtilityClass;
import dev.ultreon.libs.datetime.v0.Duration;
import org.jetbrains.annotations.NotNull;

import static dev.ultreon.bubbles.BubbleBlaster.TPS;

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

        var hour = (int) hourDouble;

        var minuteString = Integer.toString(minute);
        var secondString = Integer.toString(second);

        if (minuteString.length() == 1) minuteString = "0" + minuteString;
        if (secondString.length() == 1) secondString = "0" + secondString;

        return hour + ":" + minuteString + ":" + secondString;
    }
}
