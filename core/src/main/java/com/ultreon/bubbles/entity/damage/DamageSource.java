package com.ultreon.bubbles.entity.damage;

public class DamageSource {
    private final DamageType type;

    public DamageSource(DamageType type) {
        this.type = type;
    }

    public DamageType getType() {
        return type;
    }
}
