package dev.ultreon.bubbles.debug;

import dev.ultreon.libs.commons.v0.Identifier;

public abstract class Formatter<T> {
    private final Class<T> clazz;
    private final Identifier name;

    public Formatter(Class<T> clazz, Identifier name) {
        this.clazz = clazz;
        this.name = name;
    }

    public abstract void format(T obj, IFormatterContext context);

    public final void formatOther(Object obj, IFormatterContext context) {
        DebugRenderer.format(obj, context);
    }

    public Class<T> clazz() {
        return this.clazz;
    }

    public Identifier registryName() {
        return this.name;
    }
}
