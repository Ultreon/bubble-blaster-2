package com.ultreon.dev;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class DevClassPath extends HashMap<String, List<String>> {
    private String s;

    public DevClassPath() {
        super();
    }

    void string(String s) {
        this.s = s;
    }

    private List<File> parse(String s) {
        return Stream.of(s.split(System.getProperty("path.separator"))).map(File::new).toList();
    }

    @Deprecated
    public List<File> getFiles() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return s;
    }
}
