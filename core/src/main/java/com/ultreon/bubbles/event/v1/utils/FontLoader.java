package com.ultreon.bubbles.event.v1.utils;

import com.ultreon.libs.commons.v0.Identifier;

@FunctionalInterface
public interface FontLoader {
    void loadFont(Identifier font);
}
