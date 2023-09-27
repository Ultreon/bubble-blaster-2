package com.ultreon.bubbles.mod;

import com.badlogic.gdx.graphics.Texture;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;

public class ModDataManager {
    private static final Map<String, Texture> ICONS = Maps.newHashMap();

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public static Texture setIcon(ModContainer entry, Texture pixmap) {
        return ICONS.put(entry.getMetadata().getId(), pixmap);
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public static Texture setIcon(String id, Texture read) {
        return ICONS.put(id, read);
    }

    @CanIgnoreReturnValue
    public static Texture getIcon(ModContainer entry) {
        return Objects.requireNonNull(ICONS.get(entry.getMetadata().getId()), "Icon not found for mod: " + entry.getMetadata().getId());
    }
}
