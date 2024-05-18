package dev.ultreon.bubbles.random;

import dev.ultreon.bubbles.entity.spawning.Hashable;
import dev.ultreon.bubbles.util.Randomizer;
import dev.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.NotNull;

public interface RandomSource {
    long nextLong();
    int nextInt();
    int nextInt(int max);
    int nextInt(int min, int max);
    float nextFloat();
    float nextFloat(float max);
    float nextFloat(float min, float max);
    double nextDouble();
    double nextDouble(double max);
    double nextDouble(double min, double max);
    RandomSource nextRandom();
    default RandomSource nextRandom(@NotNull Hashable hashable) {
        return this.nextRandom(hashable.hash());
    }
    RandomSource nextRandom(long seed);
    default RandomSource nextRandom(String seed) {
        return this.nextRandom(Randomizer.hash(seed));
    }
    default RandomSource nextRandom(Identifier seed) {
        return this.nextRandom(Randomizer.hash(seed));
    }

    default boolean chance(int chance) {
        return this.nextInt(chance) == 0;
    }

    default boolean chance(double chance) {
        return this.nextDouble(1.0) <= chance;
    }

    default boolean chance(float chance) {
        return this.nextFloat(1f) <= chance;
    }
}
