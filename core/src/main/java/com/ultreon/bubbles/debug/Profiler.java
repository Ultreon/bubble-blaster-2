package com.ultreon.bubbles.debug;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Profiler {
    private final ConcurrentMap<Thread, ThreadSection> values = new ConcurrentHashMap<>();

    public Profiler() {

    }

    public void start(String name) {
        ThreadSection threadSection = values.computeIfAbsent(Thread.currentThread(), thread -> new ThreadSection(this));
        threadSection.start(name);
    }

    public void end() {
        Thread cur = Thread.currentThread();
        if (values.containsKey(cur)) values.get(cur).end();
        else values.put(cur, new ThreadSection(this));
    }

    public Map<Thread, ThreadSection> collect() {
        return Collections.unmodifiableMap(values);
    }

    public void section(String name, Runnable block) {
        start(name);
        block.run();
        end();
    }
}
