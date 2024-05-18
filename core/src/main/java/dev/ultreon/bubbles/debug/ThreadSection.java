package dev.ultreon.bubbles.debug;

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
        this.values.put(this.current.getName(), this.current);
        this.current = null;
    }

    public Map<String, Section> getValues() {
        return Collections.unmodifiableMap(this.values);
    }
}
