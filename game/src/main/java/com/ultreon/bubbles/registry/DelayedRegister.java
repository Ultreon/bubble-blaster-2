package com.ultreon.bubbles.registry;

import com.ultreon.bubbles.common.IRegistrable;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.Registrable;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.registry.object.RegistrySupplier;
import org.apache.logging.log4j.LogManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

public class DelayedRegister<@NonNull T extends IRegistrable> {
    @NonNull
    private final String modId;
    @NonNull
    private final Registry<T> registry;
    private final ArrayList<HashMap.Entry<Identifier, Supplier<T>>> objects = new ArrayList<>();

    protected DelayedRegister(@NonNull String modId, @NonNull Registry<T> registry) {
        this.modId = modId;
        this.registry = registry;
    }

    public static <T extends Registrable> DelayedRegister<T> create(String modId, Registry<T> registry) {
        return new DelayedRegister<>(modId, registry);
    }

    public <@NonNull C extends T> RegistrySupplier<C> register(@NonNull String key, @NonNull Supplier<@NonNull C> supplier) {
        Identifier rl = new Identifier(key, modId);

        objects.add(new HashMap.SimpleEntry<>(rl, supplier::get));

        return new RegistrySupplier<>(registry, supplier, rl);
    }

    public void register() {
        GameEvents.AUTO_REGISTER.listen(registry -> {
            if (!registry.getType().equals(this.registry.getType())) {
                return;
            }

            LogManager.getLogger("Registration").info("Mod " + modId + " registration for: " + registry.getType().getName());

            for (HashMap.Entry<Identifier, Supplier<T>> entry : objects) {
                T object = entry.getValue().get();
                Identifier rl = entry.getKey();

                if (!registry.getType().isAssignableFrom(object.getClass())) {
                    throw new IllegalArgumentException("Got invalid type in deferred register: " + object.getClass() + " expected assignable to " + registry.getType());
                }

                this.registry.register(rl, object);
                object.setId(rl);
            }
        });
    }
}
