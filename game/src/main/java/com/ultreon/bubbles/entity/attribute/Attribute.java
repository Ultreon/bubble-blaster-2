package com.ultreon.bubbles.entity.attribute;

import java.util.HashMap;
import java.util.regex.Pattern;

public record Attribute(String name) {
    private static final HashMap<String, Attribute> attributeMap = new HashMap<>();

    public static final Attribute MAX_HEALTH = new Attribute("generic.max_health");
    public static final Attribute DEFENSE = new Attribute("generic.defense");
    public static final Attribute ATTACK = new Attribute("generic.attack");
    public static final Attribute SPEED = new Attribute("generic.speed");
    public static final Attribute SCORE_MODIFIER = new Attribute("player.score_modifier");
    public static final Attribute SCORE = new Attribute("bubble.score");
    public static final Attribute LUCK = new Attribute("player.luck");

    public Attribute {
        if (!Pattern.matches("[a-z_]{3,}(\\.[a-z_]{3,})+", name)) {
            throw new IllegalArgumentException("Invalid attribute name: " + name);
        }

        if (Attribute.attributeMap.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate attribute detected!");
        }

        Attribute.attributeMap.put(name, this);
    }

    public static Attribute fromName(String name) {
        return Attribute.attributeMap.get(name);
    }
}
