package com.ultreon.bubbles.event.v2.utils;

import com.ultreon.bubbles.common.Identifier;

@FunctionalInterface
public interface FontLoader {
    void loadFont(Identifier font);
}
