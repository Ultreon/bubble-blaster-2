package com.ultreon.commons.time;

import com.ultreon.commons.exceptions.InvalidOrderException;

import java.io.Serializable;

public class TimeSpan implements Serializable {
    private DateTime from;
    private DateTime to;

    public TimeSpan(DateTime from, DateTime to) {
        if (from.compareTo(to) > 0) throw new InvalidOrderException("Parameter ‘from’ is later than ‘to’.");

        this.from = from;
        this.to = to;
    }

    public boolean contains(DateTime dateTime) {
        return DateTime.isBetween(this.from, this.to);
    }

    public Duration toDuration() {
        return new Duration(this.to.toEpochSeconds() - this.from.toEpochSeconds());
    }

    public DateTime getFrom() {
        return this.from;
    }

    public DateTime getTo() {
        return this.to;
    }

    public void setFrom(DateTime from) {
        this.from = from;
    }

    public void setTo(DateTime to) {
        this.to = to;
    }
}
