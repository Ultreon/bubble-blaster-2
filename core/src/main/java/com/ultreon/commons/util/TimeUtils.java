package com.ultreon.commons.util;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@SuppressWarnings("unused")
public class TimeUtils {
    private TimeUtils() {

    }

    /**
     * @param seconds the desired seconds to format.
     * @return the formatted seconds.
     */
    @Deprecated
    public static String formatDuration(long seconds) {
        return formatDuration(Duration.ofSeconds(seconds));
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
                return minute + ":" + pad(second);

            return hour + ":" + pad(minute) + ":" + pad(second);
        }

        return day + ":" + pad(hour) + ":" + pad(minute) + ":" + pad(second);
    }

    private static String pad(int value) {
        if (value < 10) return value > -10 ? "-0" + Math.abs(value) : "0" + value;

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
