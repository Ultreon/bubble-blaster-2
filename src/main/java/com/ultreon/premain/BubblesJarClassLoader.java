package com.ultreon.premain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class BubblesJarClassLoader extends ClassLoader {
    private final Class<?> reference;
    private final Set<String> jars;
    private final Map<String, Class<?>> cache = new HashMap<>();
    private final Map<String, String> classToJar = new HashMap<>();
    private final Map<String, byte[]> classData = new HashMap<>();
    private final Map<String, byte[]> resources = new HashMap<>();
    private final Map<String, URL> globalUrl = new HashMap<>();
    private final List<URL> resourceUrls = new ArrayList<>();
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

                    URL url = new URL("libraryjar", jar, "/" + entryName);
                    resources.put(jar + "/" + entryName, data);
                    globalUrl.putIfAbsent("/" + entryName, url);
                    resourceUrls.add(url);
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
        Class<?> cached = cache.get(name);
        if (cached != null) {
            return cached;
        }
        byte[] data = locateClassData(name);
        if (data != null) {
            Certificate[] certificates = classCertificates.get(name);
            URL url = locateClassURL(name);
            CodeSource source = new CodeSource(url, certificates);
            ProtectionDomain domain = new ProtectionDomain(source, getClass().getProtectionDomain().getPermissions(), this, getClass().getProtectionDomain().getPrincipals());
            Class<?> defined = defineClass(name, data, 0, data.length, domain);
            if (resolve) {
                resolveClass(defined);
            }
            cache.put(name, defined);
            return defined;
        }

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
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        return globalUrl.get(name);
    }

//    @Override
//    public Enumeration<URL> findResources(String name) {
//        return Collections.enumeration(resourceUrls);
//    }

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
