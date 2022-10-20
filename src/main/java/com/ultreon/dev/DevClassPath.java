package com.ultreon.dev;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class DevClassPath {
    private final List<File> classPath;

    public DevClassPath(@NonNull String s) {
        this.classPath = parse(s);
    }

    private List<File> parse(String s) {
        return Stream.of(s.split(System.getProperty("path.separator"))).map(File::new).toList();
    }

    public List<File> getFiles() {
        return classPath;
    }
}
