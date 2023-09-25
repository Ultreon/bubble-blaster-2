package com.ultreon.commons.time;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

/**
 * Time processing utilities.
 */
public class TimeProcessor {
    /**
     * Get the current time in seconds.
     * @return the current time.
     */
    public static double now() {
        return (double) System.nanoTime() / 1000000000d;
    }

    public static int daysToTicks(double hours) {
        return (int) (TPS * (hours * 86400));
    }

    public static int hoursToTicks(double hours) {
        return (int) (TPS * (hours * 3600));
    }

    public static int minutesToTicks(double minutes) {
        return (int) (TPS * (minutes * 60));
    }

    public static int secondsToTicks(double seconds) {
        return (int) (TPS * seconds);
    }

    public static int millisToTicks(double millis) {
        return (int) (TPS * (millis / 1000));
    }

    public static int microsToTicks(double micros) {
        return (int) (TPS * (micros / 1000000));
    }

    public static int nanosToTicks(double nanos) {
        return (int) (TPS * (nanos / 1000000000));
    }

    public static double ticksToSeconds(int ticks) {
        return ticks / (double) TPS;
    }

    /**
     * Converts speed of "value per second" to "value per tick"
     * @param perSecond the amount per second.
     * @return the amount per tick.
     */
    public static double valueToTicks(int perSecond) {
        return perSecond / (double) TPS;
    }
}
