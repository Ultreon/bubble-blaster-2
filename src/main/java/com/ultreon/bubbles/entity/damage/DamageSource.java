package com.ultreon.bubbles.entity.damage;

public class DamageSource {
    private final DamageSourceType type;

    public DamageSource(DamageSourceType type) {
        this.type = type;
    }

    public DamageSourceType getType() {
        return type;
    }
}
