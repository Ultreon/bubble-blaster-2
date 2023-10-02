package com.ultreon.bubbles.common;

public enum DifficultyEffectType {
    BUBBLE_SPEED(true, false),
    LOCAL(false, true),
    BOTH(true, true);

    private final boolean speed;
    private final boolean local;

    DifficultyEffectType(boolean speed, boolean local) {
        this.speed = speed;
        this.local = local;
    }

    public boolean isSpeed() {
        return this.speed;
    }

    public boolean isLocal() {
        return this.local;
    }
}
