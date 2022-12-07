package com.ultreon.bubbles.mod.loader;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.commons.crash.CrashCategory;
import com.ultreon.commons.crash.CrashLog;
import com.ultreon.commons.exceptions.TODO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

import static java.io.File.pathSeparator;

@AntiMod
@ApiStatus.Internal
@SuppressWarnings({"unused"})
public final class Scanner {
    private final List<URL> urls;
    private final ClassLoader classLoader;
    private final boolean isGame;
    private static final Logger logger = LogManager.getLogger("Scanner");
    private boolean annotationScan;
    private JarEntry jarEntry;
    private String className;
    private HashMap<Class<? extends Annotation>, ArrayList<Class<?>>> classes;

    @ApiStatus.Internal
    public Scanner(boolean isGame, List<URL> urls, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.urls = urls;
        this.isGame = isGame;
    }
    
    @ApiStatus.Internal
    public ScannerResult scan() {
        classes = new HashMap<>();
        className = null;
        jarEntry = null;
        annotationScan = false;

        for (URL url : urls) {
            scan(url);
        }
        
        return new ScannerResult(classes);
    }

    private void scan(URL url) {
        if (Objects.equals(url.getProtocol(), "file")) {
            try {
                scan(new File(url.toURI()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else if (Objects.equals(url.getProtocol(), "libraryjar")) {
            LibraryJar libraryJar = new LibraryJar(url);
            this.scanLibraryJar(libraryJar);
        } else if (Objects.equals(url.getProtocol(), "modfile")) {
            // TODO "The 'modfile' protocol isn't yet implemented."
            throw new TODO("The 'modfile' protocol isn't yet implemented.");
        }
    }

    private void scanLibraryJar(LibraryJar libraryJar) {
        try(JarInputStream stream = libraryJar.openStream()) {
            JarEntry je;

            annotationScan = true;

            while ((je = stream.getNextJarEntry()) != null) {
                jarEntry = je;
                scanEntry(je);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void scanEntry(JarEntry je) {
        if (je.isDirectory() || !je.getName().endsWith(".class")) {
            return;
        }

        if (isGame) {
            if (!je.getName().startsWith("com/ultreon")) {
                return;
            }
        }

        // -6 because create .class
        String className1 = je.getName().substring(0, je.getName().length() - 6);
        className = className1.replace('/', '.');
        try {
            scanClass(className);
        } catch (Throwable t) {
            logger.warn("Couldn't load class: " + className);
            t.printStackTrace();
        }
    }

    private void scan(File file) {
        try {
            try {
                BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                if (basicFileAttributes.isDirectory()) {
                    scanDir(file);
                } else if (basicFileAttributes.isRegularFile()) {
                    scanJar(file);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } catch (Throwable t) {
            CrashLog crashLog = new CrashLog("Jar file being scanned", t);
            CrashCategory modCategory = new CrashCategory("Jar entry being scanned");
            modCategory.add("Class Name", className);
            modCategory.add("Entry", jarEntry != null ? jarEntry.getName() : null);
            modCategory.add("Annotation Scan", annotationScan);
            crashLog.addCategory(modCategory);
            BubbleBlaster.crash(crashLog.createCrash());
        }
    }

    private void scanJar(File file) throws IOException {
        Enumeration<JarEntry> e;
        try (JarFile jarFile = new JarFile(file.getPath())) {
            if (!isGame) {
                e = jarFile.entries();
                while (e.hasMoreElements()) {
                    JarEntry je = e.nextElement();
                    jarEntry = je;
                    if (je.isDirectory() || !je.getName().endsWith(".class")) {
                        continue;
                    }

                    // -6 because create .class
                    String className1 = je.getName().substring(0, je.getName().length() - 6);
                    className = className1.replace('/', '.');
                    try {
                        Class<?> aClass = Class.forName(className, false, BubbleBlaster.class.getClassLoader());
                        throw new ScannerException("Class is included by Bubble Blaster: " + className);
                    } catch (ClassNotFoundException ignored) {

                    }
                }
            }

            e = jarFile.entries();

            annotationScan = true;

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                jarEntry = je;
                scanEntry(je);
            }
        }
    }

    private void scanDir(File file) throws IOException {
        if (!isGame) {
            try (var walk = Files.walk(file.toPath(), 30)) {
                for (File subFile : walk.map(Path::toFile).toList()) {
                    String substring = getRelativePath(subFile);

                    if (subFile.isDirectory() || !(subFile.getName().endsWith(".class") || subFile.getName().endsWith(".java"))) {
                        continue;
                    }

                    // -6 because create .class
                    String className1 = substring.substring(0, substring.length() - 6);
                    className = className1.replace('/', '.');
                    try {
                        Class<?> aClass = Class.forName(className);
                    } catch (ClassNotFoundException ignored) {

                    }
                }
            }
        }

        annotationScan = true;

        try (var walk = Files.walk(file.toPath(), 30, FileVisitOption.FOLLOW_LINKS)) {
            try (var walk1 = Files.walk(file.toPath(), 30, FileVisitOption.FOLLOW_LINKS)) {
                try (var walk2 = Files.walk(file.toPath(), 30, FileVisitOption.FOLLOW_LINKS)) {
                    logger.info(walk.collect(Collectors.toList()));
                    logger.info(walk1.map(Path::toFile).collect(Collectors.toList()));

                    for (var subFile : walk2.map(Path::toFile).toList()) {
                        if (subFile.isDirectory() || !(subFile.getName().endsWith(".class") || subFile.getName().endsWith(".java"))) {
                            continue;
                        }

                        var substring = getRelativePath(subFile);

                        logger.debug("Scanning file: " + subFile.getPath());

                        if (isGame) {
                            if (!substring.startsWith("com/ultreon")) {
                                continue;
                            }
                        }

                        // -6 because create .class
                        String className1 = substring.substring(0, substring.length() - 6);
                        className = className1.replace('/', '.');
                        try {
                            scanClass(className);
                        } catch (Throwable t) {
                            logger.warn("Couldn't load class: " + className);
                        }
                    }
                }
            }
        }
    }

    private void scanClass(String className) throws ClassNotFoundException {
        Class<?> c = Class.forName(className, false, classLoader);
        Annotation[] annotations = c.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (!classes.containsKey(annotation.annotationType())) {
                classes.put(annotation.annotationType(), new ArrayList<>());
            }
            classes.get(annotation.annotationType()).add(c);
        }
    }

    @NonNull
    private String getRelativePath(File file) {
        int length = pathLength(file);

        String string = file.getAbsolutePath();
        String substring = string.substring(length);
        if (substring.startsWith("/") || substring.startsWith("\\")) {
            substring = substring.substring(1);
        }

        substring = substring.replaceAll("\\\\", "/");
        return substring;
    }

    private int pathLength(File file) {
        String path = file.getAbsolutePath();
        path = path.replaceAll("/", pathSeparator);
        if (path.endsWith(pathSeparator)) {
            path += System.getProperty(pathSeparator);
        }

        return path.length();
    }

    @ApiStatus.Internal
    public List<URL> getUrls() {
        return urls;
    }
}
