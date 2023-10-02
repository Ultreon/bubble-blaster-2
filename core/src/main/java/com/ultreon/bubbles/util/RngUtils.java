package com.ultreon.bubbles.util;

import com.google.common.hash.Hashing;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.libs.commons.v0.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class RngUtils {
    public static <T> List<T> choices(Collection<T> values, int count) {
        return RngUtils.choices(values, new Random(), count);
    }

    public static <T> List<T> choices(Collection<T> values, Random random, int count) {
        List<T> list = new ArrayList<>(values);
        Collections.shuffle(list, random);
        return list.subList(0, Math.min(count, list.size()));
    }

    public static <T> T choose(List<T> values) {
        return RngUtils.choose(values, BubbleBlaster.RANDOM);
    }

    public static <T> T choose(List<T> values, Random random) {
        if (values.isEmpty()) return null;
        return values.get(random.nextInt(values.size()));
    }

    public static Random create(Random random, String seed) {
        return new Random(random.nextLong() ^ Objects.requireNonNullElseGet(Long.getLong(seed, null), () -> RngUtils.hash(seed)));
    }

    public static Random create(String seed) {
        return new Random(Objects.requireNonNullElseGet(Long.getLong(seed, null), () -> RngUtils.hash(seed)));
    }

    public static Random create(Random random, Identifier seed) {
        return new Random(random.nextLong() ^ RngUtils.hash(seed));
    }

    public static Random create(Identifier seed) {
        return new Random(RngUtils.hash(seed));
    }

    public static long hash(String seed) {
        byte[] bytes = seed.getBytes(StandardCharsets.UTF_8);
        return Hashing.murmur3_128().hashBytes(bytes).asLong();
    }

    public static long hash(int seed) {
        return Hashing.murmur3_128().hashInt(seed).asLong();
    }

    public static long hash(long seed) {
        return Hashing.murmur3_128().hashLong(seed).asLong();
    }

    private static long hash(String[] arraySeed) {
        StringBuilder sb = new StringBuilder();
        for (String element : arraySeed) {
            sb.append(element);
        }
        String concatenatedString = sb.toString();
        byte[] bytes = concatenatedString.getBytes(StandardCharsets.UTF_8);
        return Hashing.murmur3_128().hashBytes(bytes).asLong();
    }

    public static long hash(Identifier idSeed) {
        return RngUtils.hash(idSeed.toArray());
    }

    public static boolean chance(int chance) {
        return new Random().nextInt(chance) == 0;
    }

    public static boolean chance(double chance) {
        return new Random().nextDouble(1.0) <= chance;
    }

    public static boolean chance(Random random, int chance) {
        return random.nextInt(chance) == 0;
    }

    public static boolean chance(Random random, double chance) {
        return random.nextDouble(1.0) <= chance;
    }
}
