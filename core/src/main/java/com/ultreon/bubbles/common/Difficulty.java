package com.ultreon.bubbles.common;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.ultreon.libs.text.v1.Translatable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * iDifficulty enum, used as api for difficulty information.
 *
 * @author XyperCode
 * @since 0.0.0
 */
public enum Difficulty implements Translatable {
    TOO_EASY(0.02f),
    EZ_PZ(0.1f),
    EASY(0.5f),
    NORMAL(1.0f),
    HARD(4.0f),
    EXPERT(16.0f),
    ABSURD(64.0f),
    INSANITY(256.0f),
    SUPER_NATURAL(1024.0f),
    LEGEND(4096.0f),
    GG(65536.0f),
    WHAT_THE_FRICK(Integer.MAX_VALUE / 2f + 1);

    private final float plainModifier;

    Difficulty(float modifier) {
        this.plainModifier = modifier;
    }

    public float getPlainModifier() {
        return this.plainModifier;
    }

    @Override
    public String getTranslationPath() {
        return "bubbleblaster.misc.difficulty." + this.name().toLowerCase();
    }

    public enum ModifierAction {
        ADD, MULTIPLY, MULTIPLY_TOTAL
    }

    public static final class Modifier {
        private final ModifierAction action;
        private final float value;

        public Modifier(ModifierAction action, float value) {
            this.action = action;
            this.value = value;
        }

        public ModifierAction action() {
            return action;
        }

        public float value() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Modifier) obj;
            return Objects.equals(this.action, that.action) &&
                    Float.floatToIntBits(this.value) == Float.floatToIntBits(that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(action, value);
        }

        @Override
        public String toString() {
            return "Modifier[" +
                    "action=" + action + ", " +
                    "value=" + value + ']';
        }

        }

    public static final class ModifierToken {
        public ModifierToken() {
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this || obj != null && obj.getClass() == this.getClass();
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public String toString() {
            return "ModifierToken[]";
        }


        }

    public static class ModifierMap {
        private final Map<ModifierToken, Modifier> modifiers = new HashMap<>();

        public ModifierMap() {

        }

        @CanIgnoreReturnValue
        public void set(ModifierToken token, Modifier modifier) {
            this.modifiers.put(token, modifier);
        }

        @CheckReturnValue
        public Modifier get(ModifierToken token) {
            return this.modifiers.get(token);
        }

        @CanIgnoreReturnValue
        public Modifier remove(ModifierToken token) {
            return this.modifiers.remove(token);
        }

        public float modify(Difficulty difficulty) {
            float value = difficulty.getPlainModifier();
            float modifyTotal = 1;
            for (Modifier modifier : this.modifiers.values()) {
                switch (modifier.action) {
                    case ADD -> value += modifier.value;
                    case MULTIPLY -> value *= modifier.value;
                    case MULTIPLY_TOTAL -> modifyTotal *= modifier.value;
                }
            }

            return value * modifyTotal;
        }
    }
}
