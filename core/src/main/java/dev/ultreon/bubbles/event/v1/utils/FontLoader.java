package dev.ultreon.bubbles.event.v1.utils;

import dev.ultreon.libs.commons.v0.Identifier;

@FunctionalInterface
public interface FontLoader {
    void loadFont(Identifier font);
}
