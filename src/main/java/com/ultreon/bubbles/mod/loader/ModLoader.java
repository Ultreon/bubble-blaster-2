package com.ultreon.bubbles.mod.loader;

import com.google.gson.Gson;
import com.ultreon.bubbles.common.References;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.commons.lang.Messenger;
import com.ultreon.commons.lang.ProgressMessenger;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

@AntiMod
@ApiStatus.Internal
public class ModLoader {
    @ApiStatus.Internal
    private static final Gson GSON = new Gson();
    @ApiStatus.Internal
    private final URLClassLoader classLoader;
    @ApiStatus.Internal
    private final List<ModClassLoader> modClassLoaders = new ArrayList<>();
    @ApiStatus.Internal
    private final List<ModClass> modClasses = new ArrayList<>();
    @ApiStatus.Internal
    private final Map<String, ModObject> objects = new HashMap<>();
    @ApiStatus.Internal
    private final List<ModException> errors = new ArrayList<>();
    @ApiStatus.Internal
    private final List<File> files = new ArrayList<>();

    @ApiStatus.Internal
    public ModLoader(URLClassLoader loader) {
        this.classLoader = loader;
    }

    @ApiStatus.Internal
    public void scanForJars() {
        if (!References.MODS_DIR.exists()) {
            References.MODS_DIR.mkdirs();
            return;
        }
        File[] files = References.MODS_DIR.listFiles();
        if (files == null) throw new RuntimeException("Can't access mods directory: " + References.MODS_DIR.getAbsolutePath());
        for (File file : files) {
            String[] nameExt = file.getName().split("\\.");
            if (nameExt.length >= 2 && file.isFile() && Objects.equals(nameExt[nameExt.length - 1], "jar")) {
                try {
                    addJar(file);
                    this.files.add(file);
                } catch (Exception e) {
                    BubbleBlaster.getLogger().error("Error loading jar mod:", e);
                }
            } else {
                BubbleBlaster.getLogger().debug("Found invalid mod file: " + file.getPath());
            }
        }
    }

    @ApiStatus.Internal
    public void addJar(File file) throws IOException {
        try (JarFile jarFile = new JarFile(file)) {
            ZipEntry entry = jarFile.getEntry("bubbles.mod.json");
            InputStream inputStream = jarFile.getInputStream(entry);
            InputStreamReader reader = new InputStreamReader(inputStream);
            ModInfo modInfo = GSON.fromJson(reader, ModInfo.class);
            Map<String, String> entryPoints = modInfo.getEntryPoints();
            String normal = entryPoints.get("normal");
            try {
                ModClassLoader modClassLoader = new ModClassLoader(file, jarFile, modInfo, classLoader);
                this.modClassLoaders.add(modClassLoader);
                Class<?> aClass = Class.forName(normal, false, modClassLoader);
                this.modClasses.add(new ModClass(modInfo.getModId(), aClass));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Can't load jar file: " + file, e);
            }
        }
    }

    @ApiStatus.Internal
    public void scan(Messenger msgAlt, AtomicReference<ProgressMessenger> progAlt) {
        progAlt.set(new ProgressMessenger(msgAlt, modClassLoaders.size()));
        for (ModClassLoader modClassLoader : modClassLoaders) {
            ScannerResult scan = modClassLoader.scan();
            String modId = modClassLoader.getModInfo().getModId();
            objects.put(modId, new ModObject(scan, modClassLoader.getModInfo()));
        }

        buildModList();
    }

    @ApiStatus.Internal
    private void buildModList() {
        ModList.set(new ModList(objects));
    }

    @ApiStatus.Internal
    public void init(Messenger messenger, AtomicReference<ProgressMessenger> progAlt) {
        progAlt.set(new ProgressMessenger(messenger, modClasses.size()));
        for (ModClass modClass : modClasses) {
            try {
                ModLoadingContext.set(objects.get(modClass.modId()));
                modClass.init();
            } catch (NoSuchMethodException e) {
                errors.add(new ModException(e));
            }
        }
    }

    @ApiStatus.Internal
    public List<ModException> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    @ApiStatus.Internal
    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    @ApiStatus.Internal
    public List<File> getFiles() {
        return files;
    }
}
