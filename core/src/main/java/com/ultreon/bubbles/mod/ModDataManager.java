package com.ultreon.bubbles.mod;

import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Objects;

public class ModDataManager {
    private static final Map<String, BufferedImage> ICONS = Maps.newHashMap();

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public static BufferedImage setIcon(ModContainer entry, BufferedImage read) {
        return ICONS.put(entry.getMetadata().getId(), read);
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public static BufferedImage setIcon(String id, BufferedImage read) {
        return ICONS.put(id, read);
    }

    @NotNull
    @CanIgnoreReturnValue
    public static BufferedImage getIcon(ModContainer entry) {
        return Objects.requireNonNull(ICONS.get(entry.getMetadata().getId()), "Icon not found for mod: " + entry.getMetadata().getId());
    }
}
