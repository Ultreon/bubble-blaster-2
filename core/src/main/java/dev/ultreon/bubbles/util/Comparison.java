package dev.ultreon.bubbles.util;

import java.util.Collection;
import java.util.Iterator;

public class Comparison {
    public static <T> boolean isGreater(Comparable<T> obj, T than) {
        return obj.compareTo(than) > 0;
    }

    public static <T> boolean isGreaterOrEqual(Comparable<T> obj, T than) {
        return obj.compareTo(than) >= 0;
    }

    public static <T> boolean isEqual(Comparable<T> obj, T than) {
        return obj.compareTo(than) == 0;
    }

    public static <T> boolean isLessEqual(Comparable<T> obj, T than) {
        return obj.compareTo(than) <= 0;
    }

    public static <T> boolean isLess(Comparable<T> obj, T than) {
        return obj.compareTo(than) < 0;
    }

    public static <T extends Comparable<T>> T max(Collection<T> coll, T def) {
        Iterator<? extends T> i = coll.iterator();
        if (!i.hasNext()) {
            return def;
        }

        var candidate = i.next();

        while (i.hasNext()) {
            var next = i.next();
            if (next.compareTo(candidate) > 0)
                candidate = next;
        }
        return candidate;
    }
}
