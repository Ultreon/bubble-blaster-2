package com.ultreon.bubbles.registry;

import com.google.common.annotations.Beta;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.effect.StatusEffect;
import com.ultreon.bubbles.entity.ammo.AmmoType;
import com.ultreon.bubbles.entity.player.ability.AbilityType;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.item.ItemType;
import com.ultreon.bubbles.media.Sound;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.bubbles.render.font.Font;
import com.ultreon.commons.map.OrderedHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;


public class Registry<T> {
    private static final Logger dumpLogger = LogManager.getLogger("Registry-Dump");
    private final OrderedHashMap<Identifier, T> keyMap = new OrderedHashMap<>();
    private final OrderedHashMap<T, Identifier> valueMap = new OrderedHashMap<>();
    private final Class<T> type;
    private static final OrderedHashMap<Class<?>, Registry<?>> registries = new OrderedHashMap<>();
    private final Identifier id;
    private boolean frozen;

    public static final Registry<AmmoType> AMMO_TYPES = Registry.create(new Identifier("ammo"));
    public static final Registry<EntityType<?>> ENTITIES = Registry.create(new Identifier("entity"));
    public static final Registry<BubbleType> BUBBLES = Registry.create(new Identifier("bubble"));
    public static final Registry<StatusEffect> EFFECTS = Registry.create(new Identifier("status_effect"));
    public static final Registry<AbilityType<?>> ABILITIES = Registry.create(new Identifier("ability"));
    public static final Registry<GameplayEvent> GAMEPLAY_EVENTS = Registry.create(new Identifier("gameplay_event"));
    public static final Registry<Gamemode> GAMEMODES = Registry.create(new Identifier("gamemode"));
    public static final Registry<Cursor> CURSORS = Registry.create(new Identifier("cursor"));
    @Beta
    public static final Registry<ItemType> ITEMS = Registry.create(new Identifier("item"));
    public static final Registry<TextureCollection> TEXTURE_COLLECTIONS = Registry.create(new Identifier("texture_collection"));
    public static final Registry<Sound> SOUNDS = Registry.create(new Identifier("sound"));
    public static final Registry<Font> FONTS = Registry.create(new Identifier("font"));

    protected Registry(Class<T> clazz, Identifier id) throws IllegalStateException {
        this.id = id;
        this.type = clazz;

        GameEvents.REGISTRY_DUMP.listen(this::dumpRegistry);
    }

    public static Collection<Registry<?>> getRegistries() {
        return registries.values();
    }

    public void freeze() {
        this.frozen = true;
    }

    public Identifier id() {
        return id;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> Registry<T> create(Identifier registryName, @NotNull T... type) {
        Class<T> componentType = (Class<T>) type.getClass().getComponentType();
        if (registries.containsKey(componentType)) {
            throw new IllegalStateException();
        }

        Registry<T> registry = new Registry<>(componentType, registryName);
        registries.put(componentType, registry);

        return registry;
    }

    /**
     * Returns the identifier of the given registered instance.
     *
     * @param obj the registered instance.
     * @return the identifier of it.
     */
    @Nullable
    public Identifier getKey(T obj) {
        return valueMap.get(obj);
    }

    /**
     * Returns the registered instance from the given {@link Identifier}
     *
     * @param key the namespaced key.
     * @return a registered instance create the type {@link T}.
     * @throws ClassCastException if the type is invalid.
     */
    public T getValue(@Nullable Identifier key) {
        if (!keyMap.containsKey(key)) {
            throw new IllegalArgumentException("Cannot find object for: " + key + " | type: " + type.getSimpleName());
        }
        return keyMap.get(key);
    }

    public boolean contains(Identifier rl) {
        return keyMap.containsKey(rl);
    }

    public void dumpRegistry() {
        dumpLogger.info("Registry dump: " + type.getSimpleName());
        for (Map.Entry<Identifier, T> entry : entries()) {
            T object = entry.getValue();
            Identifier rl = entry.getKey();

            dumpLogger.info("  (" + rl + ") -> " + object);
        }
    }

    /**
     * Register an object.
     *
     * @param rl  the resource location.
     * @param val the register item value.
     */
    public void register(Identifier rl, T val) {
        if (!type.isAssignableFrom(val.getClass())) {
            throw new IllegalArgumentException("Not allowed type detected, got " + val.getClass() + " expected assignable to " + type);
        }

        keyMap.put(rl, val);
        valueMap.put(val, rl);
    }

    public Collection<T> values() {
        return Collections.unmodifiableCollection(keyMap.values());
    }

    public Set<Identifier> keys() {
        return Collections.unmodifiableSet(keyMap.keySet());
    }

    public Set<Map.Entry<Identifier, T>> entries() {
        // I do this because IDE won's accept dynamic values and keys.
        ArrayList<T> values = new ArrayList<>(values());
        ArrayList<Identifier> keys = new ArrayList<>(keys());

        if (keys.size() != values.size()) throw new IllegalStateException("Keys and values have different lengths.");

        Set<Map.Entry<Identifier, T>> entrySet = new HashSet<>();

        for (int i = 0; i < keys.size(); i++) {
            entrySet.add(new AbstractMap.SimpleEntry<>(keys.get(i), values.get(i)));
        }

        return Collections.unmodifiableSet(entrySet);
    }

    public Class<T> getType() {
        return type;
    }

    public static void dump() {
        if (BubbleBlaster.isDebugMode()) {
            for (Registry<?> registry : registries.values()) {
                dumpLogger.info("Registry: (" + registry.id() + ") -> {");
                dumpLogger.info("  Type: " + registry.getType().getName() + ";");
                for (Map.Entry<Identifier, ?> entry : registry.entries()) {
                    Object o = null;
                    String className = null;
                    try {
                        o = entry.getValue();
                        className = o.getClass().getName();
                    } catch (Throwable ignored) {

                    }

                    dumpLogger.info("  (" + entry.getKey() + ") -> {");
                    dumpLogger.info("    Class : " + className + ";");
                    dumpLogger.info("    Object: " + o + ";");
                    dumpLogger.info("  }");
                }
                dumpLogger.info("}");
            }
        }
    }

    public boolean isFrozen() {
        return frozen;
    }
}
