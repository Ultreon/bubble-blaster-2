package com.ultreon.bubbles.mod.loader;

import com.ultreon.preloader.PreClassLoader;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

@AntiMod
@ApiStatus.Internal
public class ModClassLoader extends ClassLoader {
    private final File file;
    private final JarFile jarFile;
    private final ModInfo modInfo;
    private final PreClassLoader classLoader;
    private final Scanner scanner;

    @ApiStatus.Internal
    public ModClassLoader(File file, JarFile jarFile, ModInfo modInfo, URLClassLoader classLoader) {
        super(classLoader);
        if (!(classLoader instanceof PreClassLoader preClassLoader)) {
            throw new IllegalArgumentException("Expected the pre class loader.");
        }

        this.file = file;
        this.jarFile = jarFile;
        this.modInfo = modInfo;
        this.classLoader = preClassLoader;

        this.scanner = new Scanner(file, this);
    }

    @ApiStatus.Internal
    public ScannerResult scan() {
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
    public File getFile() {
        return file;
    }

    @ApiStatus.Internal
    public JarFile getJarFile() {
        return jarFile;
    }

    @ApiStatus.Internal
    public ModInfo getModInfo() {
        return modInfo;
    }
}
