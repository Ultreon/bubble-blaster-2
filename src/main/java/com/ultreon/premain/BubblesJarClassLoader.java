package com.ultreon.premain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class BubblesJarClassLoader extends ClassLoader {
    private final Class<?> reference;
    private final Set<String> jars;
    private final Map<String, Class<?>> cache = new HashMap<>();
    private final Map<String, String> classToJar = new HashMap<>();
    private final Map<String, byte[]> classData = new HashMap<>();
    private final Map<String, byte[]> resources = new HashMap<>();
    private Map<String, Certificate[]> classCertificates = new HashMap<>();

    public BubblesJarClassLoader(Class<?> reference, String[] jars, ClassLoader parent) {
        super(parent);
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
                while ((e = stream.getNextJarEntry()) != null) {
                    String entryName = e.getName();
                    byte[] data = stream.readAllBytes();
                    if (entryName.endsWith(".class")) {
                        String className = entryName.substring(0, entryName.length() - 6).replaceAll("/", ".");
                        classData.put(className, data);
                        classToJar.put(className, jar);
                        classCertificates.put(className, e.getCertificates());
                    }
                    resources.put(jar + "/" + entryName, data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BubblesJarClassLoader(Class<?> reference, String[] jars) {
        this(reference, jars, BubblesJarClassLoader.class.getClassLoader());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        System.out.println("Loading class: " + name);
        Class<?> cached = cache.get(name);
        if (cached != null) {
            return cached;
        }
        byte[] data = locateClassData(name);
        if (data != null) {
            System.out.println("Located class data.");
            Certificate[] certificates = classCertificates.get(name);
            URL url = locateClassURL(name);
            System.out.println("Located class url: " + url);
            CodeSource source = new CodeSource(url, certificates);
            ProtectionDomain domain = new ProtectionDomain(source, getClass().getProtectionDomain().getPermissions(), this, getClass().getProtectionDomain().getPrincipals());
            Class<?> defined = defineClass(name, data, 0, data.length, domain);
            if (resolve) {
                resolveClass(defined);
                System.out.println("Resolved class");
            }
            System.out.println("Loaded class: " + defined.getName());
            cache.put(name, defined);
            return defined;
        }

        System.out.println("Class not in any of the jars.");
        Class<?> superLoaded = super.loadClass(name, resolve);
        cache.put(name, superLoaded);
        return superLoaded;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> cached = cache.get(name);
        if (cached != null) {
            return cached;
        }

        return super.findClass(name);
    }

    @Override
    public URL getResource(String name) {
        String[] split = name.split("/", 2);
        String path = "";
        if (split.length == 2) {
            path = split[1];
        }
        try {
            return new URL("libraryjar", split[0], path);
        } catch (MalformedURLException e) {
            throw new Error(e);
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
        System.out.println("url = " + url);
        System.out.println("url.getProtocol() = " + url.getProtocol());
        System.out.println("url.getHost() = " + url.getHost());
        System.out.println("url.getPath() = " + url.getPath());
        if (!url.getPath().isEmpty()) {
            String id = jarFile + url.getPath();
            System.out.println("id = " + id);
            byte[] bytes = resources.get(id);
            return bytes == null ? null : new ByteArrayInputStream(bytes);
        } else {
            return getClass().getResourceAsStream("/META-INF/jars/" + jarFile);
        }
    }

    boolean jarExists(String name) {
        String[] split = name.split("/", 2);
        String jarFile = split[0];
        return jars.contains(jarFile);
    }

    private byte[] locateClassData(String name) {
        return classData.get(name);
    }

    private URL locateClassURL(String name) {
        try {
            return new URL("libraryjar", classToJar.get(name), "");
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }
}
