package com.ultreon.commons.util;

import com.ultreon.libs.commons.v0.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

@SuppressWarnings("unused")
public class TimeUtils extends UtilityClass {
    private static final long MSPT = 1000 / TPS;

    private TimeUtils() {
        super();
    }

    public static Duration ofTicks(long ticks) {
        return Duration.ofMillis(ticks * MSPT);
    }

    public static long toTicks(Duration duration) {
        return duration.toMillis() / MSPT;
    }

    /**
     * @param seconds the desired seconds to format.
     * @return the formatted seconds.
     */
    @Deprecated
    public static String formatDuration(long seconds) {
        return TimeUtils.formatDuration(Duration.ofSeconds(seconds));
    }

    /**
     * @param duration the desired duration to format.
     * @return the formatted duration.
     */
    public static String formatDuration(Duration duration) {
        long day = duration.toDays();
        int hour = duration.toHoursPart();
        int minute = duration.toMinutesPart();
        int second = duration.toSecondsPart();

        if (day == 0) {
            if (hour == 0)
                return minute + ":" + TimeUtils.pad(second);

            return hour + ":" + TimeUtils.pad(minute) + ":" + TimeUtils.pad(second);
        }

        return day + ":" + TimeUtils.pad(hour) + ":" + TimeUtils.pad(minute) + ":" + TimeUtils.pad(second);
    }

    private static String pad(int value) {
        if (value < 10) return value > -10 && value < 0 ? "-0" + Math.abs(value) : "0" + value;

        return String.valueOf(value);
    }

    @NotNull
    @Deprecated
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
