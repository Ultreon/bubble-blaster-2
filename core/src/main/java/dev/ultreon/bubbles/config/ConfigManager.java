package dev.ultreon.bubbles.config;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.GamePlatform;
import dev.ultreon.bubbles.event.v1.ConfigEvents;
import dev.ultreon.bubbles.util.exceptions.DuplicateElementException;
import dev.ultreon.libs.commons.v0.UtilityClass;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager extends UtilityClass {
    private static final Map<String, Config> CONFIGS = new HashMap<>();

    private ConfigManager() {
        super();
    }

    public static void registerConfig(String namespace, Config config) {
        var modLoaded = GamePlatform.get().isModLoaded(namespace);
        if (!modLoaded) {
            BubbleBlaster.LOGGER.warn("Tried to register config for non-existing mod: " + namespace);
            return;
        }

        if (CONFIGS.containsKey(namespace)) {
            throw new DuplicateElementException("Config already registered for namespace '" + namespace + "'");
        }

        CONFIGS.put(namespace, config);

        ConfigEvents.RELOAD_ALL.listen(() -> CONFIGS.values().forEach(configToReload -> {
            configToReload.reload();
            configToReload.save();
        }));
    }
}
