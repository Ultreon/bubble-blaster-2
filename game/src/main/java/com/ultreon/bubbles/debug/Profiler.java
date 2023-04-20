package com.ultreon.bubbles.debug;

import java.util.*;

public class Profiler {
    private final Map<Thread, Map<String, Section>> values = new HashMap<>();
    private Section current;

    public Profiler() {

    }

    public void start(String name) {
        values.computeIfAbsent(Thread.currentThread(), thread -> new HashMap<>());
        if (current != null) {
            current.start(name);
            return;
        }
        current = values.computeIfAbsent(Thread.currentThread(), thread -> new HashMap<>()).computeIfAbsent(name, $ -> new Section(name, this));
        current.startThis();
    }

    public void end() {
        if (current == null) return;
        if (current.hasCurrent()) {
            current.end();
            return;
        }
        current.endThis();
        values.computeIfAbsent(Thread.currentThread(), thread -> new HashMap<>()).put(current.getName(), current);
        current = null;
    }

    public Map<Thread, Map<String, Section>> collect() {
        return Collections.unmodifiableMap(values);
    }

    public void section(String name, Runnable block) {
        start(name);
        block.run();
        end();
    }
}
