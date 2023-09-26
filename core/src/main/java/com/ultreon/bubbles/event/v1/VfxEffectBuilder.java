package com.ultreon.bubbles.event.v1;

import com.crashinvaders.vfx.effects.VfxEffect;
import com.ultreon.libs.collections.v0.maps.OrderedHashMap;

import java.util.*;

public class VfxEffectBuilder {
    private final Map<UUID, VfxEffect> filters;

    public VfxEffectBuilder() {
        this(new OrderedHashMap<>());
    }

    public VfxEffectBuilder(Map<UUID, VfxEffect> filters) {
        this.filters = filters;
    }

    public void set(UUID id, VfxEffect filter) {
        if (!this.filters.containsKey(id)) {
            this.filters.put(id, filter);
        }
    }

    public VfxEffect pop(UUID id) {
        return this.filters.remove(id);
    }

    public Map<UUID, VfxEffect> getFilters() {
        return Collections.unmodifiableMap(this.filters);
    }
}
