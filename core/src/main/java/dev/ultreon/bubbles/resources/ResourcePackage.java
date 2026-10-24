package dev.ultreon.bubbles.resources;

import dev.ultreon.libs.commons.v0.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResourcePackage {
    protected final Map<Identifier, Resource> resources;

    public ResourcePackage(Map<Identifier, Resource> resources) {
        this.resources = resources;
    }

    public ResourcePackage() {
        this.resources = new HashMap<>();
    }

    public boolean has(Identifier entry) {
        return this.resources.containsKey(entry);
    }

    public Set<Identifier> entries() {
        return this.resources.keySet();
    }

    public Resource get(Identifier entry) {
        return this.resources.get(entry);
    }

    public Map<Identifier, Resource> mapEntries() {
        return Collections.unmodifiableMap(this.resources);
    }
}
