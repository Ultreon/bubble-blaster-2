package com.ultreon.commons.time;

public enum MeteorologicalSeason {
    WINTER(),
    SPRING(),
    SUMMER(),
    AUTUMN();

    MeteorologicalSeason() {

    }

    public Date getStartDate(int year) {
        switch (this) {
            case WINTER:
                return new Date(1, 12, year);
            case SPRING:
                return new Date(1, 3, year);
            case SUMMER:
                return new Date(1, 6, year);
            case AUTUMN:
                return new Date(1, 9, year);
            default:
                throw new IllegalArgumentException();
        }
    }

    public Date getEndDate(int year) {
        switch (this) {
            case WINTER:
                return new Date(DateTime.isLeapYear(year) ? 29 : 28, 2, year);
            case SPRING:
                return new Date(31, 5, year);
            case SUMMER:
                return new Date(31, 8, year);
            case AUTUMN:
                return new Date(30, 11, year);
            default:
                throw new IllegalArgumentException();
        }
    }
}
