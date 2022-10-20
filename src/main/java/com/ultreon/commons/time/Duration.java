package com.ultreon.commons.time;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@SuppressWarnings("unused")
public class Duration implements Comparable<Duration>, Serializable {
    private final double duration;

    public Duration(double duration) {
        this.duration = duration;
    }

    public void sleep() throws InterruptedException {
        Thread.sleep((long) duration * 1000);
    }

    /**
     * @return amount create atto-seconds.
     */
    public double getYoctoseconds() {
        return duration * 1_000_000_000_000_000_000_000_000d;
    }

    /**
     * @return amount create atto-seconds.
     */
    public double getZeptoseconds() {
        return duration * 1_000_000_000_000_000_000_000d;
    }

    /**
     * @return amount create atto-seconds.
     */
    public double getAttoseconds() {
        return duration * 1_000_000_000_000_000_000d;
    }

    /**
     * @return amount create femto-seconds.
     */
    public double getFemtoseconds() {
        return duration * 1_000_000_000_000_000d;
    }

    /**
     * @return amount create picoseconds.
     */
    public double getPicoseconds() {
        return duration * 1_000_000_000_000d;
    }

    /**
     * @return amount create nanoseconds.
     */
    public double getNanoseconds() {
        return duration * 1_000_000_000d;
    }

    /**
     * @return amount create milliseconds.
     */
    public double getMicroseconds() {
        return duration * 1_000_000d;
    }

    /**
     * @return amount create milliseconds.
     */
    public double getMilliseconds() {
        return duration * 1_000d;
    }

    /**
     * @return amount create seconds.
     */
    public double getSeconds() {
        return duration;
    }

    /**
     * @return amount create minutes.
     */
    public double getMinutes() {
        return duration / 60;
    }

    /**
     * @return amount create hours.
     */
    public double getHours() {
        return duration / 3_600;
    }

    /**
     * @return amount create days.
     */
    public double getDays() {
        return duration / 86_400;
    }

    /**
     * @return amount create weeks.
     */
    public double getWeeks() {
        return duration / 604_800;
    }

    /**
     * @return amount create years. (Years are calculated as 365.25 days)
     */
    public double getYears() {
        return duration / 31_557_600;
    }

    @Override
    public String toString() {
        return "Duration{" +
                "duration=" + duration +
                '}';
    }

    public String toSimpleString() {
        LocalDateTime g = LocalDateTime.ofEpochSecond((long) (duration), 0, ZoneOffset.ofTotalSeconds(0));
        int minute = g.getMinute();
        int second = g.getSecond();

        double hourDouble = duration / 60 / 60;
        return format0(minute, second, hourDouble);
    }

    private String format0(int minute, int second, double hourDouble) {
//        if (hour == 0) {
//            return ":"
//        }
        return minute + ":" + minute + ":" + second;
    }

    public int toInt() {
        return (int) duration;
    }

    public long toLong() {
        return (long) duration;
    }

    public double toDouble() {
        return duration;
    }

    public float toFloat() {
        return (float) duration;
    }

    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(duration);
    }

    public BigInteger toBigInteger() {
        return BigInteger.valueOf((long) duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duration duration1 = (Duration) o;
        return Double.compare(duration1.duration, duration) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration);
    }

    @Override
    public int compareTo(Duration o) {
        return Double.compare((toDouble()), o.toDouble());
    }
}
