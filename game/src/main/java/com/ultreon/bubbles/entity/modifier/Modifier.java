package com.ultreon.bubbles.entity.modifier;

import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonString;

public class Modifier {
    public static final ModifierType ATTACK = new ModifierType("bubbleblaster.attack");
    public static final ModifierType DEFENSE = new ModifierType("bubbleblaster.defense");
    public static final ModifierType SCORE = new ModifierType("bubbleblaster.score");

    private final ModifierType type;
    private final double value;

    public Modifier(ModifierType type, double value) {
        this.type = type;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public BsonDocument write(BsonDocument document) {
        document.put("name", new BsonString(this.type.name()));
        document.put("value", new BsonDouble(this.value));

        return null;
    }

    public static void read(BsonDocument document) {
        ModifierType type = ModifierType.types.get(document.getString("name").getValue());
        double value = document.getDouble("value").getValue();
    }
}
