package dev.ultreon.bubbles.entity.modifier;

import java.util.HashMap;
import java.util.Objects;

public final class ModifierType {
    static final HashMap<String, ModifierType> types = new HashMap<>();
    private final String name;


    public ModifierType(String name) {
        if (types.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate modifier detected!");
        }

        ModifierType.types.put(name, this);
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ModifierType) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "ModifierType[" +
                "name=" + this.name + ']';
    }

}
