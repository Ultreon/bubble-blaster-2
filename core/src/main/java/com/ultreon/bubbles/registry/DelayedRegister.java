package com.ultreon.bubbles.registry;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.registry.object.RegistrySupplier;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

@Deprecated
public class DelayedRegister<T> {
    @NotNull
    private final String modId;
    @NotNull
    private final Registry<T> registry;
    private final ArrayList<HashMap.Entry<Identifier, Supplier<T>>> objects = new ArrayList<>();

    protected DelayedRegister(@NotNull String modId, @NotNull Registry<T> registry) {
        this.modId = modId;
        this.registry = registry;
    }

    public static <T> DelayedRegister<T> create(String modId, Registry<T> registry) {
        return new DelayedRegister<>(modId, registry);
    }

    public <C extends T> RegistrySupplier<C> register(@NotNull String key, @NotNull Supplier<@NotNull C> supplier) {
        Identifier id = new Identifier(this.modId, key);

        this.objects.add(new HashMap.SimpleEntry<>(id, supplier::get));

        return new RegistrySupplier<>(this.registry, supplier, id);
    }

    public void register() {
        GameEvents.AUTO_REGISTER.listen(registry -> {
            if (!registry.getType().equals(this.registry.getType())) {
                return;
            }

            LogManager.getLogger("Registration").info("Mod " + this.modId + " registration for: " + registry.getType().getName());

            for (HashMap.Entry<Identifier, Supplier<T>> entry : this.objects) {
                T object = entry.getValue().get();
                Identifier id = entry.getKey();

                if (!registry.getType().isAssignableFrom(object.getClass())) {
                    throw new IllegalArgumentException("Got invalid type in deferred register: " + object.getClass() + " expected assignable to " + registry.getType());
                }

                this.registry.register(id, object);
            }
        });
    }
}
