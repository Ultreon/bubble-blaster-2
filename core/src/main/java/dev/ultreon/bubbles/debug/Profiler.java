package dev.ultreon.bubbles.debug;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Profiler {
    private final ConcurrentMap<Thread, ThreadSection> values = new ConcurrentHashMap<>();

    public Profiler() {

    }

    public void start(String name) {
        var threadSection = this.values.computeIfAbsent(Thread.currentThread(), thread -> new ThreadSection(this));
        threadSection.start(name);
    }

    public void end() {
        var cur = Thread.currentThread();
        if (this.values.containsKey(cur)) this.values.get(cur).end();
        else this.values.put(cur, new ThreadSection(this));
    }

    public Map<Thread, ThreadSection> collect() {
        return Collections.unmodifiableMap(this.values);
    }

    public void section(String name, Runnable block) {
        this.start(name);
        block.run();
        this.end();
    }
}
