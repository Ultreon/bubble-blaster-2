package com.ultreon.bubbles.registry;

import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.NotNull;

public interface RegisterHandler {
    void onRegister(@NotNull Identifier id);
}
