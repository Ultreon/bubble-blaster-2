package com.ultreon.bubbles.resources;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.commons.exceptions.SyntaxException;
import com.ultreon.commons.function.ThrowingSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceManager {
    private final Map<File, PathMapping<byte[]>> mapping = new ConcurrentHashMap<>();
    private final Map<Identifier, byte[]> assets = new ConcurrentHashMap<>();
    private final List<ResourcePackage> resourcePackages = new ArrayList<>();
    private final Logger logger = LogManager.getLogger("Resource-Manager");

    public byte[] getResource(String path) {
        List<byte[]> collect = mapping.values().stream().map((mapping) -> mapping.get(path)).toList();
        if (collect.size() == 0) {
            return null;
        }
        return collect.get(collect.size() - 1);
    }

    public InputStream openResourceStream(String path) {
        byte[] resource = getResource(path);
        return resource == null ? null : new ByteArrayInputStream(resource);
    }

    public InputStream openResourceStream(Identifier entry) {
        @Nullable Resource resource = getResource(entry);
        return resource == null ? null : resource.openStream();
    }

    @Nullable
    public Resource getResource(Identifier entry) {
        for (ResourcePackage resourcePackage : resourcePackages) {
            if (resourcePackage.has(entry)) {
                return resourcePackage.get(entry);
            }
        }

        logger.warn("Unknown resource: " + entry);


        return null;
    }

    public void dump() {
        for (ResourcePackage resourcePackage : resourcePackages) {
            resourcePackage.dump();
        }
    }

    public void importResources(File file) {
        if (!file.exists()) {
            BubbleBlaster.getInstance().crash(new IOException("Resources file doesn't exists: " + file.getAbsolutePath()));
        }

        if (file.isFile()) {
            importFileResourcePackage(file);
        } else if (file.isDirectory()) {
            importDirResourcePackage(file);
        }
    }

    @SuppressWarnings("unused")
    private void importDirResourcePackage(File file) {
        // Check if it's a directory.
        assert file.isDirectory();

        try {
            // Prepare (entry -> resource) mappings/
            Map<Identifier, Resource> map = new HashMap<>();

            // Get assets directory.
            File assets = new File(file, "assets");

            // Check if assets directory exists.
            if (assets.exists()) {
                // List files in assets dir.
                File[] files = assets.listFiles();

                // Loop listed files.
                for (File assetsPackage : files != null ? files : new File[0]) {
                    // Get assets-package namespace from the name create the listed file (that's a dir).
                    String namespace = assetsPackage.getName();

                    // Walk assets package.
                    try (Stream<Path> walk = Files.walk(assetsPackage.toPath())) {
                        for (Path assetPath : walk.toList()) {
                            // Convert to file object.
                            File asset = assetPath.toFile();

                            // Check if it's a file, if not we will walk to the next file / folder in the Files.walk(...) list.
                            if (!asset.isFile()) {
                                continue;
                            }

                            // Create resource with file input stream.
                            ThrowingSupplier<InputStream, IOException> sup = () -> new FileInputStream(asset);
                            Resource resource = new Resource(sup);

                            // Continue to next file / folder if asset path is the same path as the assets package.
                            if (assetPath.toFile().equals(assetsPackage)) {
                                continue;
                            }

                            // Calculate resource path.
                            Path relative = assetsPackage.toPath().relativize(assetPath);
                            String s = relative.toString().replaceAll("\\\\", "/");

                            // Create resource entry/
                            Identifier entry;
                            try {
                                entry = new Identifier(s, namespace);
                            } catch (SyntaxException e) {
                                logger.error("Invalid resource identifier.", e);
                                continue;
                            }

                            // MEME
                            boolean b = Person.MY_SELF.kill(Person.MY_SELF) == Emotion.LOL;

                            // Add resource mapping for (entry -> resource).
                            map.put(entry, resource);
                        }
                    }
                }

                resourcePackages.add(new ResourcePackage(map));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importFileResourcePackage(File file) {
        // Check if it's a file.
        assert file.isFile();

        // Check for .jar files.
        if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
            // Prepare (entry -> resource) mappings.
            Map<Identifier, Resource> map = new HashMap<>();

            // Create jar file instance from file.
            try {
                @SuppressWarnings("resource") ZipFile jarFile = new ZipFile(file); // Shouldn't be closed.
                // Get jar entries, and convert it into an iterable to use in for(...) loops
                Enumeration<? extends ZipEntry> var0 = jarFile.entries();

                // Loop jar entries.
                while (var0.hasMoreElements()) {
                    ZipEntry jarEntry = var0.nextElement();
                    // Get name create the jar entry.
                    String name = jarEntry.getName();

                    // Check if it isn't a directory, because we want a file.
                    if (!jarEntry.isDirectory()) {
                        String[] splitPath = name.split("/", 3);

                        if (splitPath.length >= 3) {
                            String assetsDirectoryName = splitPath[0];
                            if (assetsDirectoryName.equals("assets")) {
                                // Get namespace and path from split path
                                String namespace = splitPath[1];
                                String path = splitPath[2];

                                // Resource
                                ThrowingSupplier<InputStream, IOException> sup = () -> jarFile.getInputStream(jarEntry);
                                Resource resource = new Resource(sup);

                                try {
                                    // Entry
                                    Identifier entry = new Identifier(path, namespace);

                                    // Add (entry -> resource) mapping.
                                    map.put(entry, resource);
                                } catch (Throwable ignored) {

                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            resourcePackages.add(new ResourcePackage(map));
        }
    }

    public byte[] getAsset(Identifier identifier) {
        return assets.get(identifier);
    }

    public InputStream getAssetAsStream(Identifier identifier) {
        return new ByteArrayInputStream(getAsset(identifier));
    }

    public List<byte[]> getAllAssetsByPath(String path) {
        List<byte[]> data = new ArrayList<>();
        for (ResourcePackage resourcePackage : resourcePackages) {
            Map<Identifier, Resource> identifierResourceMap = resourcePackage.mapEntries();
            for (Map.Entry<Identifier, Resource> entry : identifierResourceMap.entrySet()) {
                if (entry.getKey().path().equals(path)) {
                    byte[] bytes = entry.getValue().loadOrGet();
                    if (bytes == null) continue;

                    data.add(entry.getValue().getData());
                }
            }
        }

        return data;
    }

    public Map<Identifier, byte[]> getAssets() {
        return assets;
    }

    private static class PathMapping<T> {
        private final Map<String, T> mapping = new ConcurrentHashMap<>();

        public PathMapping() {

        }

        public T get(String path) {
            return mapping.get(path);
        }

        public void map(String path, T type) {
            mapping.put(path, type);
        }

        public List<Map.Entry<String, T>> entries() {
            return new ArrayList<>(mapping.entrySet());
        }
    }
}
