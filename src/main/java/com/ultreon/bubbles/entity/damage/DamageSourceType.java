package com.ultreon.bubbles.entity.damage;

import java.util.Objects;

public class DamageSourceType {
    // Types
    public static final DamageSourceType COLLISION = new DamageSourceType("collision");
    public static final DamageSourceType POISON = new DamageSourceType("poison");
    public static final DamageSourceType UNKNOWN = new DamageSourceType("unknown");
    public static final DamageSourceType ATTACK = new DamageSourceType("attack");

    private final String name;

    protected DamageSourceType(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DamageSourceType that = (DamageSourceType) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
