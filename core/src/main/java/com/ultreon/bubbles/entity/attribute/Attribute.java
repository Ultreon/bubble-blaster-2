package com.ultreon.bubbles.entity.attribute;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public final class Attribute {
    private static final HashMap<String, Attribute> attributeMap = new HashMap<>();

    public static final Attribute MAX_HEALTH = new Attribute("generic.max_health");
    public static final Attribute DEFENSE = new Attribute("generic.defense");
    public static final Attribute ATTACK = new Attribute("generic.attack");
    public static final Attribute SPEED = new Attribute("generic.speed");
    public static final Attribute SCORE_MODIFIER = new Attribute("player.score_modifier");
    public static final Attribute SCORE = new Attribute("bubble.score");
    public static final Attribute LUCK = new Attribute("player.luck");
    private final String name;


    public Attribute(String name) {
        if (!Pattern.matches("[a-z_]{3,}(\\.[a-z_]{3,})+", name)) {
            throw new IllegalArgumentException("Invalid attribute name: " + name);
        }

        if (Attribute.attributeMap.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate attribute detected!");
        }

        Attribute.attributeMap.put(name, this);
        this.name = name;
    }

    public static Attribute fromName(String name) {
        return Attribute.attributeMap.get(name);
    }

    public static Set<String> names() {
        return Attribute.attributeMap.keySet();
    }

    public String name() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Attribute that = (Attribute) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "Attribute[" +
                "name=" + this.name + ']';
    }

}
