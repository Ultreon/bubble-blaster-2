package com.ultreon.bubbles.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionsUtils {
    public static <T extends Comparable<T>> T max(Collection<T> coll, T def) {
        Iterator<? extends T> i = coll.iterator();
        if (!i.hasNext()) {
            return def;
        }

        T candidate = i.next();

        while (i.hasNext()) {
            T next = i.next();
            if (next.compareTo(candidate) > 0)
                candidate = next;
        }
        return candidate;
    }
}
