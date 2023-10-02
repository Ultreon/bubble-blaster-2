package com.ultreon.bubbles.entity.damage;

import java.util.Objects;

public class DamageType {
    // Types
    public static final DamageType COLLISION = new DamageType("collision");
    public static final DamageType POISON = new DamageType("poison");
    public static final DamageType UNKNOWN = new DamageType("unknown");
    public static final DamageType ATTACK = new DamageType("attack");

    private final String name;

    protected DamageType(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        DamageType that = (DamageType) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
