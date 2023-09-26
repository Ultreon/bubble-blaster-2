package com.ultreon.bubbles.util;

import java.util.*;

public class RngUtils {
    public static <T> List<T> choices(Collection<T> values, int count) {
        return choices(values, new Random(), count);
    }

    public static <T> List<T> choices(Collection<T> values, Random random, int count) {
        List<T> list = new ArrayList<>(values);
        Collections.shuffle(list, random);
        return list.subList(0, Math.min(count, list.size()));
    }
}
