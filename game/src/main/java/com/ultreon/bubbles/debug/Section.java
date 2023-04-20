package com.ultreon.bubbles.debug;

import java.util.*;

public class Section {
    private long start;
    private final String name;
    private long end;
    private final Map<String, Section> values = new HashMap<>();
    private final Profiler profiler;
    private Section current;

    public Section(String name, Profiler profiler) {
        this.profiler = profiler;
        this.name = name;
    }

    void startThis() {
        this.start = System.currentTimeMillis();
        this.end = 0;
    }

    void endThis() {
        this.end = System.currentTimeMillis();
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getMillis() {
        return end - start;
    }

    public void start(String name) {
        current = values.computeIfAbsent(name, $ -> new Section(name, profiler));
        current.startThis();
    }

    public void end() {
        current.endThis();
        values.put(name, current);
        current = null;
    }

    public Map<String, Section> getValues() {
        return Collections.unmodifiableMap(values);
    }

    public String getName() {
        return name;
    }

    public boolean hasCurrent() {
        return current != null;
    }
}
