package com.ultreon.bubbles.debug;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThreadSection {
    private final Profiler profiler;
    private Section current;
    private final Map<String, Section> values = new HashMap<>();

    public ThreadSection(Profiler profiler) {
        this.profiler = profiler;
    }

    public void start(String name) {
        if (current != null) {
            current.start(name);
            return;
        }
        current = values.computeIfAbsent(name, $ -> new Section(name, profiler));
        current.startThis();
    }

    public void end() {
        if (current == null) return;
        if (current.hasCurrent()) {
            current.end();
            return;
        }
        current.endThis();
        values.put(current.getName(), current);
        current = null;
    }

    public Map<String, Section> getValues() {
        return Collections.unmodifiableMap(values);
    }
}
