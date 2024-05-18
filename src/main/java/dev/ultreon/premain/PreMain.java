package dev.ultreon.premain;

import java.net.URLClassLoader;
import java.util.Objects;

public class PreMain {
    private static URLClassLoader loader;
    private static LibraryJarManager manager;

    public static void main(String[] args) {
        try {
            var c = PreMain.class;
            String files;
            try (var resourceAsStream = c.getResourceAsStream("/META-INF/jar-files.txt")) {
                files = new String(Objects.requireNonNull(resourceAsStream).readAllBytes());
            }

            String mainClass;
            try (var resourceAsStream = c.getResourceAsStream("/META-INF/main-class.txt")) {
                mainClass = new String(Objects.requireNonNull(resourceAsStream).readAllBytes());
            }

            var names = files.split("\n");

            manager = new LibraryJarManager(PreMain.class, names);
            loader = new URLClassLoader(manager.getUrls());

            var aClass = loader.loadClass(mainClass);
            var main = aClass.getDeclaredMethod("main", String[].class);
            main.invoke(null, (Object) args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preLaunch() {

    }

    static URLClassLoader getLoader() {
        return loader;
    }

    static LibraryJarManager getManager() {
        return manager;
    }
}
