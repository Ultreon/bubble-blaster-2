package com.ultreon.bubbles.common;

import com.google.common.annotations.Beta;
import org.jetbrains.annotations.Nullable;

/**
 * Not yet used.
 */
@Beta
public enum ResourceType {
    ASSETS("assets"),
    DATA("Data"),
    META_INF("META_INF"),
    OBJECT(null);

    @Nullable
    private final String path;

    ResourceType(@Nullable String path) {
        this.path = path;
    }

    @Nullable
    public String getPath() {
        return path;
    }
}
