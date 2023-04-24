package com.ultreon.bubbles.registry.object;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.registry.Registry;

import java.util.function.Supplier;

@SuppressWarnings({"unchecked"})
@Deprecated
public class RegistrySupplier<B> {
    private final Registry<? super B> registry;
    private final Supplier<B> supplier;
    private final Identifier identifier;

    public <T extends B> RegistrySupplier(Registry<? super B> registry, Supplier<B> supplier, Identifier identifier) {
        this.registry = registry;
        this.supplier = supplier;
        this.identifier = identifier;
    }

    public void register() {
        registry.register(identifier, supplier.get());
    }

    @SuppressWarnings("unchecked")
    public B get() {
        return (B) registry.getValue(identifier);
    }

    public Identifier id() {
        return identifier;
    }
}
