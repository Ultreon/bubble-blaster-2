package com.ultreon.bubbles.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base registry.
 *
 * @param <K> The type to use for values.. (KT = Key Type)
 * @param <V> The type for the registry. (VT = Value Type)
 */
public abstract class AbstractRegistry<K, V> {
    public static AbstractRegistry<?, ?> INSTANCE;

    protected final HashMap<K, V> registry = new HashMap<>();

    public AbstractRegistry() throws IllegalStateException {

    }

    /**
     * Check if there's already an instance created create the registry.
     *
     * @param instance The registry instance to check.
     */
    public final void checkInstance(AbstractRegistry<K, V> instance) {
        if (instance != null) {
            throw new IllegalStateException("Already created instance");
        }
    }

    public abstract V get(K obj);

    public abstract void register(K key, V val);

    public abstract Collection<V> values();

    public abstract Set<K> keys();

    public abstract Set<Map.Entry<K, V>> entries() throws IllegalAccessException;
}
