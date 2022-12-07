package com.ultreon.commons.time;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.chrono.IsoChronology;
import java.util.Objects;

@SuppressWarnings("unused")
public class DateTime implements Comparable<DateTime>, Serializable {
    private static final Duration DURATION = new Duration(0.0d);
    private int hour;
    private int minute;
    private int second;

    private int day;
    private int year;
    private Month month;

    public static DateTime current() {
        LocalDateTime dateTime = LocalDateTime.now();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();

        int day = dateTime.getDayOfMonth();
        int month = dateTime.getMonthValue();
        int year = dateTime.getYear();

        return new DateTime(day, month, year, hour, minute, second);
    }

    public static boolean isLeapYear(int year) {
        return IsoChronology.INSTANCE.isLeapYear(year);
    }

    public long toEpochSeconds() {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(year, month.getIndex(), day), LocalTime.of(hour, minute, second));
        return localDateTime.toEpochSecond(ZoneOffset.ofTotalSeconds(0));
    }

    public DateTime(Date date, Time time) {
        this(date.getDay(), date.getMonth(), date.getYear(), time.getHour(), time.getMinute(), time.getSecond());
    }

    public DateTime(int day, int month, int year, int hour, int minute, int second) {
        this(day, Month.from(month), year, hour, minute, second);
    }

    public DateTime(int day, Month month, int year, int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    /*************************************************************
     * Return flag meaning the object is between time1 and time2.
     *
     * @param lo low value.
     * @param hi high value.
     * @return true if the object is between time1 and time2.
     * @throws NullPointerException if ‘lo’ is higher than ‘hi’.
     */
    public static boolean isBetween(DateTime lo, DateTime hi) {
        if (lo.toEpochSeconds() > hi.toEpochSeconds()) throw new NullPointerException("‘lo’ is higher than ‘hi’");

        return ((lo.toEpochSeconds() <= hi.toEpochSeconds()) && (hi.toEpochSeconds() >= lo.toEpochSeconds()));
    }

    /**
     * Value between 0 and 23
     *
     * @return the hour.
     */
    public int getHour() {
        return hour;
    }

    /**
     * Value between 0 and 23
     *
     * @param hour value to set.
     */
    public void setHour(int hour) {
        if (hour < 0 || hour > 23) throw new IllegalArgumentException("Hour must be between 0 and 23");
        this.hour = hour;
    }

    /**
     * Value between 0 and 59.
     *
     * @return the minute.
     */
    public int getMinute() {
        return minute;
    }

    /**
     * @param minute
     */
    public void setMinute(int minute) {
        if (minute < 0 || minute > 59) throw new IllegalArgumentException("Minute must be between 0 and 23");
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        if (second < 0 || second > 59) throw new IllegalArgumentException("Second must be between 0 and 23");
        this.second = second;
    }

    /**
     * @return
     */
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        int days = getMonth().getDays(year);
        if (minute < 1 || minute > days) throw new IllegalArgumentException("Minute must be between 1 and " + days);
        this.day = day;
    }

    public int getMonthIndex() {
        return month.getIndex();
    }

    public void setMonthIndex(int index) {
        this.month = Month.from(index);
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Time getTime() {
        return new Time(hour, minute, second);
    }

    public Date getDate() {
        return new Date(day, month, year);
    }

    public Duration getDuration() {
        return DURATION;
    }

    public LocalTime toLocalTime() {
        return LocalTime.of(hour, minute, second);
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(year, month.getIndex(), day);
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(year, month.getIndex(), year, year, hour, minute, second);
    }

    @Override
    public int compareTo(DateTime o) {
        return Long.compare(toEpochSeconds(), o.toEpochSeconds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHour(), getMinute(), getSecond(), getDay(), getMonthIndex(), getYear());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateTime dateTime = (DateTime) o;
        return getHour() == dateTime.getHour() &&
                getMinute() == dateTime.getMinute() &&
                getSecond() == dateTime.getSecond() &&
                getDay() == dateTime.getDay() &&
                getMonthIndex() == dateTime.getMonthIndex() &&
                getYear() == dateTime.getYear();
    }
}
