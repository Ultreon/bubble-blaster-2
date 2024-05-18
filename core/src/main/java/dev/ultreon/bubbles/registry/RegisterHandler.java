package dev.ultreon.bubbles.registry;

import dev.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.NotNull;

public interface RegisterHandler {
    void onRegister(@NotNull Identifier id);
}
