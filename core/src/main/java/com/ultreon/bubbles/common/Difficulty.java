package com.ultreon.bubbles.common;

import com.ultreon.bubbles.common.gamestate.GameplayEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Difficulty enum, used as api for difficulty information.
 *
 * @author Qboi
 * @since 0.0.0
 */
public enum Difficulty {
    BABY(0.0625f),  // Too easy
    EASY(0.5f),
    NORMAL(1.0f),
    HARD(4.0f),
    EXPERT(16.0f),
    APOCALYPSE(64.0f),
    IMPOSSIBLE(4096.0f);

    private final float plainModifier;

    Difficulty(float modifier) {
        this.plainModifier = modifier;
    }

    public float getPlainModifier() {
        return plainModifier;
    }

    public record Modifier<T>(T key, float value) {
        public record Type<T>(Class<T> type) {
            public static final Type<GameplayEvent> GAME_EVENT = new Type<>(GameplayEvent.class);

            private static final HashMap<Class<?>, Type<?>> registry = new HashMap<>();

            public static <T> Type<T> register(Type<T> type) {
                registry.put(type.type(), type);
                return type;
            }

            @SuppressWarnings("unchecked")
            public static <T> Type<T> get(Class<T> type) {
                return (Type<T>) registry.get(type);
            }
        }
    }

    public static class ModifierMap {
        private final Map<Modifier.Type<?>, List<Modifier<?>>> modifiers = new HashMap<>();

        public ModifierMap() {

        }

        public <T> Modifier<T> add(Modifier.Type<T> type, Modifier<T> modifier) {
            modifiers.computeIfAbsent(type, t -> new ArrayList<>()).add(modifier);
            return modifier;
        }

        @SuppressWarnings("unchecked")
        public <T> List<Modifier<T>> getAll(Modifier.Type<T> type) {
            return modifiers.get(type).stream().map(o -> (Modifier<T>) o).toList();
        }
    }
}