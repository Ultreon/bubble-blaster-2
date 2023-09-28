package com.ultreon.bubbles.config;

import com.electronwill.nightconfig.core.io.ParsingException;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.event.v1.ConfigEvents;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.commons.exceptions.DuplicateElementException;
import com.ultreon.libs.commons.v0.UtilityClass;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager extends UtilityClass {
    private static final Map<String, Config> CONFIGS = new HashMap<>();

    private ConfigManager() {
        super();
    }

    public static void registerConfig(String namespace, Config config) {
        boolean modLoaded = FabricLoader.getInstance().isModLoaded(namespace);
        if (!modLoaded) return;

        if (CONFIGS.containsKey(namespace)) {
            throw new DuplicateElementException("Config already registered for namespace '" + namespace + "'");
        }

        CONFIGS.put(namespace, config);

        ConfigEvents.RELOAD_ALL.listen(() -> {
            CONFIGS.values().forEach(configToReload -> {
                try {
                    configToReload.reload();
                } catch (ParsingException e) {
                    String fileName = configToReload.getFile().getName();
                    BubbleBlaster.LOGGER.error("Failed to load config '" + fileName + "'", e);
                    BubbleBlaster.getInstance().notifications.notify(new Notification("Config Failed to Load!", "Failed to load configToReload '" + fileName + "'"));
                    File backupFile = new File(configToReload.getFile().getParentFile(), fileName + ".bak");
                    if (backupFile.exists()) {
                        BubbleBlaster.LOGGER.warn("Backup of config '" + fileName + "' already exists!");
                    }

                    try {
                        Files.copy(configToReload.getFile().toPath(), backupFile.toPath());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                configToReload.save();
            });
        });
    }
}
