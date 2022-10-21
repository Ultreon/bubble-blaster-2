package com.ultreon.bubbles.mod.loader;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScannerResult {
    private final HashMap<Class<? extends Annotation>, ArrayList<Class<?>>> classes;

    public ScannerResult(HashMap<Class<? extends Annotation>, ArrayList<Class<?>>> classes) {
        this.classes = classes;
    }

    @SuppressWarnings({"UnusedReturnValue"})
    public List<Class<?>> getClasses(Class<? extends Annotation> annotation) {
        if (!this.classes.containsKey(annotation)) {
            return new ArrayList<>();
        }

        return this.classes.get(annotation);
    }
}
