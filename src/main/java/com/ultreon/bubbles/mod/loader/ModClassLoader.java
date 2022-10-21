package com.ultreon.bubbles.mod.loader;

import com.ultreon.preloader.PreClassLoader;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@AntiMod
@ApiStatus.Internal
public class ModClassLoader extends URLClassLoader {
    private final List<File> files;
    private final ModInfo modInfo;
    private final PreClassLoader classLoader;
    private final Scanner scanner;

    @ApiStatus.Internal
    public ModClassLoader(File file, ModInfo modInfo, URLClassLoader classLoader) {
        this(List.of(file), modInfo, classLoader);
    }

    @ApiStatus.Internal
    public ModClassLoader(List<File> files, ModInfo modInfo, URLClassLoader classLoader) {
        super(getUrls(files), classLoader);
        if (!(classLoader instanceof PreClassLoader preClassLoader)) {
            throw new IllegalArgumentException("Expected the pre class loader.");
        }

        this.files = files;
        this.modInfo = modInfo;
        this.classLoader = preClassLoader;

        this.scanner = new Scanner(false, files, this);
    }

    private static URL[] getUrls(List<File> files) {
        return files.stream().map(file -> {
            try {
                return new URL("jar:" + file.toURI().toURL() + "!/");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).toList().toArray(new URL[]{});
    }

    @ApiStatus.Internal
    public ScannerResult scan() {
        for (URL url : getURLs()) {
            classLoader.addURL(url);
        }
        return scanner.scan();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loaded = super.loadClass(name, false);
        if (classLoader.isInternalPackage(name) && loaded.isAnnotationPresent(AntiMod.class)) {
            throw new ClassNotFoundException("Access denied to class marked as anti-mod.");
        }
        return super.loadClass(name, true);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> found = super.findClass(name);
        if (classLoader.isInternalPackage(name) && found.isAnnotationPresent(AntiMod.class)) {
            throw new ClassNotFoundException("Access denied to class marked as anti-mod.");
        }
        return found;
    }

    @ApiStatus.Internal
    public List<File> getFile() {
        return files;
    }

    @ApiStatus.Internal
    public ModInfo getModInfo() {
        return modInfo;
    }
}
