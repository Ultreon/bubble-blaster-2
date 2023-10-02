package com.ultreon.premain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

class LibraryJarManager {
    private final Class<?> reference;
    private final Set<String> jars;
    private final List<URL> libUrls = new ArrayList<>();
    private final Map<String, String> classToJar = new HashMap<>();
    private final Map<String, byte[]> classData = new HashMap<>();
    private final Map<String, byte[]> resources = new HashMap<>();
    private final List<URL> resourceUrls = new ArrayList<>();
    private final Map<String, Certificate[]> classCertificates = new HashMap<>();

    public LibraryJarManager(Class<?> reference, String[] jars) {
        this.reference = reference;
        this.jars = Set.of(jars);

        this.openStreams();
    }

    private void openStreams() {
        for (String jar : this.jars) {
            try {
                InputStream resource = this.reference.getResourceAsStream("/META-INF/jars/" + jar);
                if (resource == null) continue;
                JarInputStream stream = new JarInputStream(resource);
                JarEntry e;

                URL libUrl = new URL("libraryjar", jar, "");
                this.libUrls.add(libUrl);
                while ((e = stream.getNextJarEntry()) != null) {
                    String entryName = e.getName();
                    byte[] data = stream.readAllBytes();
                    if (entryName.endsWith(".class")) {
                        String className = entryName.substring(0, entryName.length() - 6).replaceAll("/", ".");
                        this.classData.put(className, data);
                        this.classToJar.put(className, jar);
                        this.classCertificates.put(className, e.getCertificates());
                    }

                    URL url = new URL("libraryjar", jar, "/" + entryName);
                    this.resources.put(jar + "/" + entryName, data);
                    this.resourceUrls.add(url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String stripLeadingSep(String s) {
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    InputStream openConnectionInternal(URL url) {
        String jarFile = url.getHost();
        if (!url.getPath().isEmpty()) {
            String id = jarFile + url.getPath();
            byte[] bytes = this.resources.get(id);
            return bytes == null ? null : new ByteArrayInputStream(bytes);
        } else {
            return this.getClass().getResourceAsStream("/META-INF/jars/" + jarFile);
        }
    }

    boolean jarExists(String name) {
        String[] split = name.split("/", 2);
        String jarFile = split[0];
        return this.jars.contains(jarFile);
    }

    public Map<String, byte[]> getResources() {
        return this.resources;
    }

    public List<URL> getResourceUrls() {
        return this.resourceUrls;
    }

    private byte[] locateClassData(String name) {
        return this.classData.get(name);
    }

    private URL locateClassURL(String name) {
        try {
            return new URL("libraryjar", this.classToJar.get(name), "");
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

    public URL[] getUrls() {
        return this.libUrls.toArray(new URL[0]);
    }
}
