package dev.ultreon.bubbles;

import org.checkerframework.common.value.qual.IntRange;

public class Ticker {
    @IntRange(from = 0)
    int ticks;

    public Ticker() {

    }

    @IntRange(from = 1)
    public int advance() {
        return ++this.ticks;
    }

    @IntRange(from = 0)
    public int get() {
        return this.ticks;
    }

    public void reset() {
        this.ticks = 0;
    }
}
