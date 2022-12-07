package com.ultreon.premain;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Objects;

public class PreMain {
    private static URLClassLoader loader;
    private static LibraryJarManager manager;

    public static void main(String[] args) {
        try {
            Class<PreMain> c = PreMain.class;
            String files;
            try (InputStream resourceAsStream = c.getResourceAsStream("/META-INF/jar-files.txt")) {
                files = new String(Objects.requireNonNull(resourceAsStream).readAllBytes());
            }

            String mainClass;
            try (InputStream resourceAsStream = c.getResourceAsStream("/META-INF/main-class.txt")) {
                mainClass = new String(Objects.requireNonNull(resourceAsStream).readAllBytes());
            }

            String[] names = files.split("\n");

            manager = new LibraryJarManager(PreMain.class, names);
            loader = new URLClassLoader(manager.getUrls());

            URL url = new URL("libraryjar://ant-launcher-1.10.12.jar/META-INF/NOTICE.txt");
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            String notice = new String(bytes);
            System.out.println(notice);

            Class<?> aClass = loader.loadClass(mainClass);
            Method main = aClass.getDeclaredMethod("main", String[].class);
            main.invoke(null, (Object) args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static URLClassLoader getLoader() {
        return loader;
    }

    static LibraryJarManager getManager() {
        return manager;
    }
}
