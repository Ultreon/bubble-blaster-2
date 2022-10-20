package com.ultreon.preloader;

import it.unimi.dsi.fastutil.bytes.ByteArrays;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Manifest;

@SuppressWarnings({"unused", "deprecation", "MismatchedQueryAndUpdateOfCollection"})
public class PreClassLoader extends URLClassLoader {
    public static final int READ_BUFFER_SIZE = 1 << 12;
    private final List<URL> sources;
    private final ClassLoader parentClassLoader = getClass().getClassLoader();

    private final Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();
    private final Set<String> invalidClassesCache = new HashSet<>(1000);

    private final Set<String> internalPackages = new HashSet<>();
    private final Map<Package, Manifest> packageManifests = new ConcurrentHashMap<>();
    private final Map<String, byte[]> dataCache = new ConcurrentHashMap<>(1000);
    private final Set<String> invalidDataCache = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final Manifest EMPTY_MANIFEST = new Manifest();

    private final AtomicReference<byte[]> readBuffer = new AtomicReference<>();

    @SuppressWarnings("SpellCheckingInspection")
    public PreClassLoader(URL[] sources) {
        super(sources, null);
        this.sources = new ArrayList<>(Arrays.asList(sources));

        // classloader exclusions
        addInternalPackage("java.");
        addInternalPackage("javax.");
        addInternalPackage("javafx.");
        addInternalPackage("javax.jmdns.");
        addInternalPackage("it.unimi.dsi.");
        addInternalPackage("com.jhlabs.");
        addInternalPackage("com.sun.");
        addInternalPackage("com.google.");
        addInternalPackage("org.fusesource.");
        addInternalPackage("org.lwjgl.");
        addInternalPackage("org.apache.");
        addInternalPackage("org.apache.batik.");
        addInternalPackage("org.apache.logging.");
        addInternalPackage("com.ultreon.bubbles.");
        addInternalPackage("com.ultreon.preloader.");
        addInternalPackage("com.ultreon.dev.");
        addInternalPackage("com.ultreon.bubbles.");
        addInternalPackage("com.ultreon.preloader.");
        addInternalPackage("com.ultreon.dev.");
    }

    @Override
    public Class<?> findClass(final String name) throws ClassNotFoundException {
        if (invalidClassesCache.contains(name)) {
            throw new ClassNotFoundException(name);
        }

        for (final String pkg : internalPackages) {
            if (name.startsWith(pkg)) {
                return parentClassLoader.loadClass(name);
            }
        }

        if (cachedClasses.containsKey(name)) {
            return cachedClasses.get(name);
        }

        try {
            // Check for cached classes.
            if (cachedClasses.containsKey(name)) {
                return cachedClasses.get(name);
            }

            // Get the last dot in the name. Used to get the package name.
            int lastDot = name.lastIndexOf('.');

            // Get package name and filename.
            String packageName = lastDot == -1 ? "" : name.substring(0, lastDot);
            String dataLocation = name.replace('.', '/').concat(".class");

            // Get url connections for the file got from the filename.
            URLConnection urlConnection = getResourceConnection(dataLocation);

            // Check for external.
            if (lastDot > 0 && !name.startsWith("com.ultreon.bubbles.")) {
                Package pkg = getPackage(packageName);
                if (pkg == null) { // Package is non-existent.
                    definePackage(packageName, null, null, null, null, null, null, null);
                } else if (pkg.isSealed()) { // Note: Found online that this might help with some issues, not sure why.
                    PreGameLoader.LOGGER.warn(String.format("External url got a sealed path %s", Objects.requireNonNull(urlConnection).getURL()));
                }
            }

            // Get class bytes.
            byte[] classBytes = getClassBytes(name);

            // Get code source and define the class.
            CodeSource codeSource = urlConnection == null ? null : new CodeSource(urlConnection.getURL(), (CodeSigner[]) null);
            Class<?> clazz = defineClass(name, classBytes, 0, classBytes.length, codeSource);

            // Cache class.
            cachedClasses.put(name, clazz);

            // Return class.
            return clazz;
        } catch (Throwable e) {
            invalidClassesCache.add(name);
            throw new ClassNotFoundException(name, e);
        }
    }

    private URLConnection getResourceConnection(final String name) {
        final URL resource = findResource(name);
        if (resource != null) {
            try {
                return resource.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    public void addURL(final URL url) {
        super.addURL(url);
        sources.add(url);
    }

    @Nonnull
    public List<URL> getSources() {
        return sources;
    }

    private byte[] readComplete(InputStream stream) {
        try {
            byte[] buffer = getOrCreateBuffer();

            int read;
            int totalLength = 0;
            while ((read = stream.read(buffer, totalLength, buffer.length - totalLength)) != -1) {
                totalLength += read;

                // Extend the buffer
                if (totalLength >= buffer.length - 1) {
                    buffer = ByteArrays.grow(buffer, buffer.length + READ_BUFFER_SIZE);
                }
            }

            final byte[] result = new byte[totalLength];
            System.arraycopy(buffer, 0, result, 0, totalLength);
            return result;
        } catch (Throwable t) {
            PreGameLoader.LOGGER.warn("Problem loading class, throwable follows:", t);
            return new byte[0];
        }
    }

    private byte[] getOrCreateBuffer() {
        byte[] buffer = readBuffer.get();
        if (buffer == null) {
            readBuffer.set(new byte[READ_BUFFER_SIZE]);
            buffer = readBuffer.get();
        }
        return buffer;
    }

    public void addInternalPackage(String packageName) {
        internalPackages.add(packageName);
    }

    public byte[] getClassBytes(String name) throws IOException {
        if (invalidDataCache.contains(name)) {
            return null;
        } else if (dataCache.containsKey(name)) {
            return dataCache.get(name);
        }

        InputStream classStream = null;
        try {
            String resourcePath = name.replace('.', '/').concat(".class");
            URL classResource = findResource(resourcePath);

            // Check for if the class resource is null.
            if (classResource == null) {
                invalidDataCache.add(name);
                return null;
            }
            classStream = classResource.openStream();

            // Read class stream.
            byte[] data = readComplete(classStream);

            // Store class data in resource cache.
            dataCache.put(name, data);

            // Return data.
            return data;
        } finally {
            if (classStream != null) {
                closeIgnoringThrowables(classStream);
            }
        }
    }

    private static void closeIgnoringThrowables(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {

        }
    }

    public void clearInvalidDataCache(Set<String> entriesToClear) {
        invalidDataCache.removeAll(entriesToClear);
    }

    public void clearInvalidDataCache() {
        invalidDataCache.clear();
    }

    public boolean isInternalPackage(String s) {
        if (!s.endsWith(".")) {
            s = s + ".";
        }

        for (String internalPackage : internalPackages) {
            if (!internalPackage.endsWith(".")) {
                internalPackage = internalPackage + ".";
            }

            if (internalPackage.startsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
