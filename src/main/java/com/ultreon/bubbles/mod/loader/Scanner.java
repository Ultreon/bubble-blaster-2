package com.ultreon.bubbles.mod.loader;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.commons.crash.CrashCategory;
import com.ultreon.commons.crash.CrashLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.io.File.pathSeparator;

@SuppressWarnings({"unused", "resource"})
public final class Scanner {
    private final File file = BubbleBlaster.getInstance().getGameFile();
    private final ClassLoader classLoader;
    private final boolean isGame = true;
    private JarFile jarFile;
    private static final Logger logger = LogManager.getLogger("Scanner");
    private boolean annotationScan;
    private JarEntry jarEntry;
    private String className;
    private HashMap<Class<? extends Annotation>, ArrayList<Class<?>>> classes;

    public Scanner(File file, ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Result scan() {
        classes = new HashMap<>();
        className = null;
        jarEntry = null;
        annotationScan = false;

        try {
            Enumeration<JarEntry> e;
            File[] files;

            try {
                BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                if (basicFileAttributes.isDirectory()) {
                    scanDirectory();
                } else if (basicFileAttributes.isRegularFile()) {
                    scanJarFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } catch (Throwable t) {
            CrashLog crashLog = new CrashLog("Jar File being scanned", t);
            CrashCategory modCategory = new CrashCategory("Jar Entry being scanned");
            modCategory.add("Class Name", className);
            modCategory.add("Entry", jarEntry != null ? jarEntry.getName() : null);
            modCategory.add("Annotation Scan", annotationScan);
            crashLog.addCategory(modCategory);
            BubbleBlaster.getInstance().crash(crashLog.createCrash());
        }
        return new Result(this, classes);
    }

    private void scanJarFile() throws IOException {
        Enumeration<JarEntry> e;
        jarFile = new JarFile(file.getPath());
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
                    Class<?> aClass = Class.forName(className);
                } catch (ClassNotFoundException ignored) {

                }
            }
        }

        e = jarFile.entries();

        annotationScan = true;

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            jarEntry = je;
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }

            if (isGame) {
                if (!je.getName().startsWith("com/ultreon")) {
                    continue;
                }
            }

            // -6 because create .class
            String className1 = je.getName().substring(0, je.getName().length() - 6);
            className = className1.replace('/', '.');
            try {
                scanClass(className);
            } catch (Throwable t) {
                logger.error("Couldn't load class: " + className);
                t.printStackTrace();
                continue;
            }
            logger.info("Scanned: " + je.getName());
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

    private void scanDirectory() throws IOException {
        if (!isGame) {
            for (File file : Files.walk(file.toPath(), 30)
                    .map(Path::toFile).toList()) {

                String substring = getRelativePath(file);

                if (file.isDirectory() || !(file.getName().endsWith(".class") || file.getName().endsWith(".java"))) {
                    continue;
                }

                logger.debug("Checking: " + file.getPath());

                // -6 because create .class
                String className1 = substring.substring(0, substring.length() - 6);
                className = className1.replace('/', '.');
                try {
                    Class<?> aClass = Class.forName(className);
                } catch (ClassNotFoundException ignored) {

                }
            }
        }

        annotationScan = true;

        Stream<Path> walk = Files.walk(file.toPath(), 30, FileVisitOption.FOLLOW_LINKS);
        Stream<Path> walk1 = Files.walk(file.toPath(), 30, FileVisitOption.FOLLOW_LINKS);
        Stream<Path> walk2 = Files.walk(file.toPath(), 30, FileVisitOption.FOLLOW_LINKS);
        logger.info(walk.collect(Collectors.toList()));
        logger.info(walk1.map(Path::toFile).collect(Collectors.toList()));

        for (File file : walk2.map(Path::toFile).toList()) {
            if (file.isDirectory() || !(file.getName().endsWith(".class") || file.getName().endsWith(".java"))) {
                continue;
            }

            String substring = getRelativePath(file);

            logger.debug("Scanning file: " + file.getPath());

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
                logger.debug("Couldn't load class: " + className);
            }
        }
    }

    @NonNull
    private String getRelativePath(File file) {
        int length = pathLength();

        String string = file.getAbsolutePath();
        String substring = string.substring(length);
        if (substring.startsWith("/") || substring.startsWith("\\")) {
            substring = substring.substring(1);
        }

        substring = substring.replaceAll("\\\\", "/");
        return substring;
    }

    private int pathLength() {
        String path = this.file.getAbsolutePath();
        path = path.replaceAll("/", pathSeparator);
        if (path.endsWith(pathSeparator)) {
            path += System.getProperty(pathSeparator);
        }

        return path.length();
    }

    public File getFile() {
        return file;
    }

    @Nullable
    public JarFile getJarFile() {
        return jarFile;
    }

    public static class Result {
        private final HashMap<Class<? extends Annotation>, ArrayList<Class<?>>> classes;
        private final Scanner scanner;

        public Result(Scanner scanner, HashMap<Class<? extends Annotation>, ArrayList<Class<?>>> classes) {
            this.classes = classes;
            this.scanner = scanner;
        }

        @SuppressWarnings({"UnusedReturnValue"})
        public List<Class<?>> getClasses(Class<? extends Annotation> annotation) {
            if (!this.classes.containsKey(annotation)) {
                return new ArrayList<>();
            }

            return this.classes.get(annotation);
        }

        public Scanner getScanner() {
            return scanner;
        }
    }

}
