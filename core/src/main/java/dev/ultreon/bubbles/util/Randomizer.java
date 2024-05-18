package dev.ultreon.bubbles.util;

import com.google.common.hash.Hashing;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.libs.commons.v0.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Randomizer {
    public static <T> List<T> choices(Collection<T> values, int count) {
        return Randomizer.choices(values, new Random(), count);
    }

    public static <T> List<T> choices(Collection<T> values, Random random, int count) {
        List<T> list = new ArrayList<>(values);
        Collections.shuffle(list, random);
        return list.subList(0, Math.min(count, list.size()));
    }

    public static <T> T choose(List<T> values) {
        return Randomizer.choose(values, BubbleBlaster.RANDOM);
    }

    public static <T> T choose(List<T> values, Random random) {
        if (values.isEmpty()) return null;
        return values.get(random.nextInt(values.size()));
    }

    public static Random create(Random random, String seed) {
        return new Random(random.nextLong() ^ Objects.requireNonNullElseGet(Long.getLong(seed, null), () -> Randomizer.hash(seed)));
    }

    public static Random create(String seed) {
        return new Random(Objects.requireNonNullElseGet(Long.getLong(seed, null), () -> Randomizer.hash(seed)));
    }

    public static Random create(Random random, Identifier seed) {
        return new Random(random.nextLong() ^ Randomizer.hash(seed));
    }

    public static Random create(Identifier seed) {
        return new Random(Randomizer.hash(seed));
    }

    public static long hash(String seed) {
        var bytes = seed.getBytes(StandardCharsets.UTF_8);
        return Hashing.murmur3_128().hashBytes(bytes).asLong();
    }

    public static long hash(int seed) {
        return Hashing.murmur3_128().hashInt(seed).asLong();
    }

    public static long hash(long seed) {
        return Hashing.murmur3_128().hashLong(seed).asLong();
    }

    private static long hash(String[] arraySeed) {
        var sb = new StringBuilder();
        for (var element : arraySeed) {
            sb.append(element);
        }
        var concatenatedString = sb.toString();
        var bytes = concatenatedString.getBytes(StandardCharsets.UTF_8);
        return Hashing.murmur3_128().hashBytes(bytes).asLong();
    }

    public static long hash(Identifier idSeed) {
        return Randomizer.hash(idSeed.toArray());
    }

    public static boolean chance(int chance) {
        return new Random().nextInt(chance) == 0;
    }

    public static boolean chance(double chance) {
        return new Random().nextDouble() <= chance;
    }

    public static boolean chance(Random random, int chance) {
        return random.nextInt(chance) == 0;
    }

    public static boolean chance(Random random, double chance) {
        return random.nextDouble() <= chance;
    }
}
