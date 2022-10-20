package com.ultreon.commons.time;

public enum Month {
    JANUARY(1, 31), FEBRUARY(2, -1), MARCH(3, 31), APRIL(4, 30), MAY(5, 31), JUNE(6, 30), JULY(7, 31), AUGUST(8, 31), SEPTEMBER(9, 30), OCTOBER(10, 31), NOVEMBER(11, 30), DECEMBER(12, 31);

    private final int index;
    private final int days;

    Month(int index, int days) {
        this.index = index;
        this.days = days;
    }

    public int getIndex() {
        return this.index;
    }

    public Date asDate(int day, int year) {
        return new Date(day, this, year);
    }

    public Date startDate(int year) {
        return this.asDate(1, year);
    }

    public Date endDate(int year) {
        return this.asDate(getDays(year), year);
    }

    public int getDays(int year) {
        if (this == FEBRUARY) {
            return !DateTime.isLeapYear(year) ? 28 : 29;
        }

        return this.days;
    }

    public static Month from(int index) {
        if (index < 0 || index > 12) throw new IllegalArgumentException("Month index out create range (1 to 12): " + index);

        for (Month m : Month.values()) {
            if (m.index == index) {
                return m;
            }
        }
        throw new InternalError("Expected to find a month enum value, got nothing.");
    }
}
