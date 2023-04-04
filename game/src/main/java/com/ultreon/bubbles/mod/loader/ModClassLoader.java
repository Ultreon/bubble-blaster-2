package com.ultreon.bubbles.mod.loader;

import com.ultreon.preloader.PreClassLoader;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;

@AntiMod
@ApiStatus.Internal
public class ModClassLoader extends URLClassLoader {
    private final List<URL> urls;
    private final ModInfo modInfo;
    private final PreClassLoader classLoader;
    private final Scanner scanner;

    @ApiStatus.Internal
    public ModClassLoader(URL url, ModInfo modInfo, URLClassLoader classLoader) {
        this(List.of(url), modInfo, classLoader);
    }

    @ApiStatus.Internal
    public ModClassLoader(List<URL> urls, ModInfo modInfo, URLClassLoader classLoader) {
        super(getUrls(urls), classLoader);
        if (!(classLoader instanceof PreClassLoader preClassLoader)) {
            throw new IllegalArgumentException("Expected the pre class loader.");
        }

        this.urls = urls;
        this.modInfo = modInfo;
        this.classLoader = preClassLoader;

        this.scanner = new Scanner(false, classLoader, urls);
    }

    private static URL[] getUrls(List<URL> urls) {
        return urls.stream().map(url -> {
            try {
                if (Objects.equals(url.getProtocol(), "file")) {
                    try {
                        File file = new File(url.toURI());
                        if (file.isFile()) {
                            return new URL("jar:" + url.toURI().toURL() + "!/");
                        } else {
                            return url;
                        }
                    } catch (URISyntaxException e) {
                        return url;
                    }
                } else {
                    return url;
                }
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
    public List<URL> getUrls() {
        return urls;
    }

    @ApiStatus.Internal
    public ModInfo getModInfo() {
        return modInfo;
    }
}
