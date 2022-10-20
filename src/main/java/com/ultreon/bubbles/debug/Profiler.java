package com.ultreon.bubbles.debug;

import it.unimi.dsi.fastutil.objects.Reference2LongArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongMaps;

public class Profiler {
    private final Reference2LongMap<String> values = new Reference2LongArrayMap<>();

    private final Reference2LongMap<String> start = new Reference2LongArrayMap<>();

    public Profiler() {
        this.values.clear();
        this.start.clear();
    }

    public void start() {
    }

    public void startSection(String name) {
        start.put(name, System.currentTimeMillis());
    }

    public void endSection(String name) {
        long start = this.start.removeLong(name);
        long end = System.currentTimeMillis();
        long time = end - start;
        values.put(name, time);
    }

    public Reference2LongMap<String> end() {
        return Reference2LongMaps.unmodifiable(values);
    }

    public void section(String name, Runnable block) {
        startSection(name);
        block.run();
        endSection(name);
    }
}
