package com.ultreon.bubbles.registry;

import com.ultreon.bubbles.common.IRegistrable;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.Registrable;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.commons.map.OrderedHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;


@SuppressWarnings("unchecked")
public class Registry<T extends IRegistrable> {
    private static final Logger dumpLogger = LogManager.getLogger("Registry-Dump");
    private final OrderedHashMap<Identifier, T> registry = new OrderedHashMap<>();
    private final Class<T> type;
    private static final OrderedHashMap<Class<?>, Registry<?>> registries = new OrderedHashMap<>();
    private final Identifier id;
    private boolean frozen;

    protected Registry(@NonNull Class<T> clazz, Identifier id) throws IllegalStateException {
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

    public static <T extends Registrable> Registry<T> create(@NonNull Class<T> clazz, Identifier registryName) {
        if (registries.containsKey(clazz)) {
            throw new IllegalStateException();
        }

        Registry<T> registry = new Registry<>(clazz, registryName);
        registries.put(clazz, registry);

        return registry;
    }

    public static <T extends Registrable> Registry<T> getRegistry(Class<T> objType) {
        return (Registry<T>) registries.get(objType);
    }

    /**
     * Returns the registered instance from the given {@link Identifier}
     *
     * @param key the namespaced key.
     * @return a registered instance create the type {@link T}.
     * @throws ClassCastException if the type is invalid.
     */
    public T get(@Nullable Identifier key) {
        if (!registry.containsKey(key)) {
            throw new IllegalArgumentException("Cannot find object for: " + key + " | type: " + type.getSimpleName());
        }
        return registry.get(key);
    }

    public boolean contains(Identifier rl) {
        return registry.containsKey(rl);
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

        registry.put(rl, val);
    }

    public Collection<T> values() {
        return Collections.unmodifiableCollection(registry.values());
    }

    public Set<Identifier> keys() {
        return Collections.unmodifiableSet(registry.keySet());
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

    public void registrable(Identifier rl, Registrable object) {
        if (type.isAssignableFrom(object.getClass())) {
            register(rl, (T) object);
        }
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
