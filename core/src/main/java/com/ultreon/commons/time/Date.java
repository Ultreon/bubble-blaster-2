package com.ultreon.commons.time;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.Era;
import java.time.chrono.IsoChronology;
import java.util.Objects;

import static com.ultreon.commons.time.MeteorologicalSeason.*;

@SuppressWarnings("unused")
public class Date implements Serializable, Comparable<Date> {
    private int day;
    private Month month;
    private int year;

    public static Date current() {
        LocalDateTime dateTime = LocalDateTime.now();
        int day = dateTime.getDayOfMonth();
        int month = dateTime.getMonthValue();
        int year = dateTime.getYear();

        return new Date(day, Month.from(month), year);
    }

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = Month.from(month);
        this.year = year;
    }

    public Date(int day, Month month, int year) {
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
    public static boolean isBetween(Date lo, Date hi) {
        if (lo.toEpochDay() > hi.toEpochDay()) throw new NullPointerException("‘lo’ is higher than ‘hi’");

        return ((lo.toEpochDay() <= hi.toEpochDay()) && (hi.toEpochDay() >= lo.toEpochDay()));
    }

    private long toEpochDay() {
        LocalDate of = LocalDate.of(year, month.getIndex(), day);
        return of.toEpochDay();
    }

    private long toEpochSecond() {
        LocalDate of = LocalDate.of(year, month.getIndex(), day);
        return of.toEpochDay() * 24 * 60 * 60;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

    public DayOfWeek getDayOfWeek() {
        LocalDate localDate = getLocalDate();
        return localDate.getDayOfWeek();
    }

    public int getDayOfYear() {
        LocalDate localDate = getLocalDate();
        return localDate.getDayOfYear();
    }

    public Era getEra() {
        LocalDate localDate = getLocalDate();
        return localDate.getEra();
    }

    public MeteorologicalSeason getSeason() {
        if (isBetween(WINTER.getStartDate(year), WINTER.getEndDate(year))) return WINTER;
        if (isBetween(SPRING.getStartDate(year), SPRING.getEndDate(year))) return SPRING;
        if (isBetween(SUMMER.getStartDate(year), SUMMER.getEndDate(year))) return SUMMER;
        if (isBetween(AUTUMN.getStartDate(year), AUTUMN.getEndDate(year))) return AUTUMN;

        throw new IllegalArgumentException("Expected to find season, but was outside any create the seasons.");
    }

    public IsoChronology getChronology() {
        LocalDate localDate = getLocalDate();
        return localDate.getChronology();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean isLeapYear() {
        LocalDate localDate = getLocalDate();
        localDate.toString();
        return localDate.getChronology().isLeapYear(getYear());
    }

    private LocalDate getLocalDate() {
        return LocalDate.of(year, month.getIndex(), day);
    }

    private LocalDateTime getLocalDateTime() {
        return getStart().toLocalDateTime();
    }

    private DateTime getStart() {
        return new DateTime(day, month, year, 0, 0, 0);
    }

    private DateTime getEnd() {
        return new DateTime(day, month, year, 23, 59, 59);
    }

    public TimeSpan toTimeSpan() {
        return new TimeSpan(getStart(), getEnd());
    }

    @Override
    public int compareTo(@NotNull Date o) {
        return Long.compare(toEpochDay(), toEpochDay());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return day == date.day &&
                month == date.month &&
                year == date.year;
    }

    public boolean equalsIgnoreYear(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return day == date.day &&
                month == date.month;
    }
}
