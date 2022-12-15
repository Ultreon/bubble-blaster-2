package com.ultreon.premain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

class LibraryJarManager {
    private final Class<?> reference;
    private final Set<String> jars;
    private final Map<String, byte[]> dataPaths = new HashMap<>();
    private final List<URL> dataUrls = new ArrayList<>();
    private final List<URL> libUrls = new ArrayList<>();

    public LibraryJarManager(Class<?> reference, String[] jars) {
        this.reference = reference;
        this.jars = Set.of(jars);

        openStreams();
    }

    private void openStreams() {
        for (String jar : jars) {
            try {
                InputStream resource = reference.getResourceAsStream("/META-INF/jars/" + jar);
                if (resource == null) continue;
                JarInputStream stream = new JarInputStream(resource);
                JarEntry e;

                URL libUrl = new URL("libraryjar", jar, "");
                libUrls.add(libUrl);
                while ((e = stream.getNextJarEntry()) != null) {
                    String entryName = e.getName();
                    byte[] data = stream.readAllBytes();

                    URL url = new URL("libraryjar", jar, "/" + entryName);
                    dataPaths.put(jar + "/" + entryName, data);
                    dataUrls.add(url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    InputStream openConnectionInternal(URL url) {
        String jarFile = url.getHost();
        if (!url.getPath().isEmpty()) {
            String id = jarFile + url.getPath();
            byte[] bytes = dataPaths.get(id);
            return bytes == null ? null : new ByteArrayInputStream(bytes);
        } else {
            return getClass().getResourceAsStream("/META-INF/jars/" + jarFile);
        }
    }

    int contentLength(URL url) {
        String jarFile = url.getHost();
        if (!url.getPath().isEmpty()) {
            String id = jarFile + url.getPath();
            byte[] bytes = dataPaths.get(id);
            return bytes == null ? -1 : bytes.length;
        }
        return -1;
    }

    boolean jarExists(String name) {
        String[] split = name.split("/", 2);
        String jarFile = split[0];
        return jars.contains(jarFile);
    }

    public Map<String, byte[]> getDataPaths() {
        return dataPaths;
    }

    public List<URL> getDataUrls() {
        return dataUrls;
    }

    public URL[] getUrls() {
        return libUrls.toArray(new URL[0]);
    }
}
