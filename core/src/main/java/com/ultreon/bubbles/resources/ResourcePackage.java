package com.ultreon.bubbles.resources;

import com.ultreon.libs.commons.v0.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Deprecated
public class ResourcePackage {
    private final Map<Identifier, Resource> resources;

    public ResourcePackage(Map<Identifier, Resource> resources) {
        this.resources = resources;
    }

    public ResourcePackage() {
        resources = new HashMap<>();
    }

    public boolean has(Identifier entry) {
        return resources.containsKey(entry);
    }

    public Set<Identifier> entries() {
        return resources.keySet();
    }

    public Resource get(Identifier entry) {
        return resources.get(entry);
    }

    public void dump() {
        System.out.println(resources);
    }

    public Map<Identifier, Resource> mapEntries() {
        return Collections.unmodifiableMap(resources);
    }
}
