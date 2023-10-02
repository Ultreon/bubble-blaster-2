package com.ultreon.bubbles.debug;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        return this.start;
    }

    public long getEnd() {
        return this.end;
    }

    public long getMillis() {
        return this.end - this.start;
    }

    public void start(String name) {
        if (this.current != null) {
            this.current.start(name);
            return;
        }
        this.current = this.values.computeIfAbsent(name, $ -> new Section(name, this.profiler));
        this.current.startThis();
    }

    public void end() {
        if (this.current == null) return;
        if (this.current.hasCurrent()) {
            this.current.end();
            return;
        }
        this.current.endThis();
        this.values.put(this.name, this.current);
        this.current = null;
    }

    public Map<String, Section> getValues() {
        return Collections.unmodifiableMap(this.values);
    }

    public String getName() {
        return this.name;
    }

    public boolean hasCurrent() {
        return this.current != null;
    }

    @Override
    public String toString() {
        return "Section{" +
                "name='" + this.name + '\'' +
                '}';
    }
}
