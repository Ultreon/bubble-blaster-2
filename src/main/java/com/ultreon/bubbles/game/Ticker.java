package com.ultreon.bubbles.game;

import org.checkerframework.common.value.qual.IntRange;

public class Ticker {
    @IntRange(from = 0)
    int ticks;

    public Ticker() {

    }

    @IntRange(from = 1)
    public int advance() {
        return ++ticks;
    }

    @IntRange(from = 0)
    public int get() {
        return ticks;
    }

    public void reset() {
        ticks = 0;
    }
}
