package com.ultreon.commons.time;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.chrono.IsoChronology;
import java.util.Objects;

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
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(this.year, this.month.getIndex(), this.day), LocalTime.of(this.hour, this.minute, this.second));
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
        return this.hour;
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
        return this.minute;
    }

    /**
     */
    public void setMinute(int minute) {
        if (minute < 0 || minute > 59) throw new IllegalArgumentException("Minute must be between 0 and 23");
        this.minute = minute;
    }

    public int getSecond() {
        return this.second;
    }

    public void setSecond(int second) {
        if (second < 0 || second > 59) throw new IllegalArgumentException("Second must be between 0 and 23");
        this.second = second;
    }

    /**
     */
    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        int days = this.getMonth().getDays(this.year);
        if (this.minute < 1 || this.minute > days) throw new IllegalArgumentException("Minute must be between 1 and " + days);
        this.day = day;
    }

    public int getMonthIndex() {
        return this.month.getIndex();
    }

    public void setMonthIndex(int index) {
        this.month = Month.from(index);
    }

    public Month getMonth() {
        return this.month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Time getTime() {
        return new Time(this.hour, this.minute, this.second);
    }

    public Date getDate() {
        return new Date(this.day, this.month, this.year);
    }

    public Duration getDuration() {
        return DURATION;
    }

    public LocalTime toLocalTime() {
        return LocalTime.of(this.hour, this.minute, this.second);
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(this.year, this.month.getIndex(), this.day);
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(this.year, this.month.getIndex(), this.year, this.year, this.hour, this.minute, this.second);
    }

    @Override
    public int compareTo(DateTime o) {
        return Long.compare(this.toEpochSeconds(), o.toEpochSeconds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getHour(), this.getMinute(), this.getSecond(), this.getDay(), this.getMonthIndex(), this.getYear());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        DateTime dateTime = (DateTime) o;
        return this.getHour() == dateTime.getHour() &&
                this.getMinute() == dateTime.getMinute() &&
                this.getSecond() == dateTime.getSecond() &&
                this.getDay() == dateTime.getDay() &&
                this.getMonthIndex() == dateTime.getMonthIndex() &&
                this.getYear() == dateTime.getYear();
    }
}
