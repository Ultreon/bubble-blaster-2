package com.ultreon.bubbles.registry.object;

import com.ultreon.bubbles.common.IRegistrable;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.Registrable;
import com.ultreon.bubbles.registry.Registry;

import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RegistrySupplier<B extends IRegistrable> {
    private final Registry registry;
    private final Supplier<B> supplier;
    private final Identifier identifier;

    public <T extends B> RegistrySupplier(Registry<?> registry, Supplier<B> supplier, Identifier identifier) {
        this.registry = registry;
        this.supplier = supplier;
        this.identifier = identifier;
    }

    public void register() {
        registry.register(identifier, (Registrable) ((Supplier) supplier).get());
    }

    @SuppressWarnings("unchecked")
    public B get() {
        return (B) registry.get(identifier);
    }

    public Identifier id() {
        return identifier;
    }
}
