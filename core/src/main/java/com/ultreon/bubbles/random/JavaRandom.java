package com.ultreon.bubbles.random;

import com.ultreon.bubbles.util.RandomChoices;
import com.ultreon.libs.commons.v0.Identifier;

import java.util.Random;

public class JavaRandom implements SeededRandomSource {
    private final long seed;
    private final Random random;

    public JavaRandom(long seed) {
        this.seed = seed;
        this.random = new Random();
    }

    public JavaRandom() {
        this(RandomChoices.hash(System.nanoTime()));
    }

    @Override
    public long nextLong() {
        return this.random.nextLong();
    }

    @Override
    public long nextLong(long max) {
        return max * this.random.nextLong();
    }

    @Override
    public long nextLong(long min, long max) {
        return (max - min) * this.random.nextLong() + min;
    }

    @Override
    public int nextInt() {
        return this.random.nextInt();
    }

    @Override
    public int nextInt(int max) {
        return max * this.random.nextInt();
    }

    @Override
    public int nextInt(int min, int max) {
        return (max - min) * this.random.nextInt() + min;
    }

    @Override
    public float nextFloat() {
        return this.random.nextFloat();
    }

    @Override
    public float nextFloat(float max) {
        return max * this.random.nextFloat();
    }

    @Override
    public float nextFloat(float min, float max) {
        return (max - min) * this.random.nextFloat() + min;
    }

    @Override
    public double nextDouble() {
        return this.random.nextDouble();
    }

    @Override
    public double nextDouble(double max) {
        return max * this.random.nextDouble();
    }

    @Override
    public double nextDouble(double min, double max) {
        return (max - min) * this.random.nextDouble() + min;
    }

    @Override
    public RandomSource nextRandom() {
        return new JavaRandom(this.random.nextLong());
    }

    @Override
    public RandomSource nextRandom(long seed) {
        return new JavaRandom(this.random.nextLong() ^ seed);
    }

    @Override
    public RandomSource nextRandom(String seed) {
        return new JavaRandom(this.random.nextLong() ^ RandomChoices.hash(seed));
    }

    @Override
    public RandomSource nextRandom(Identifier seed) {
        return new JavaRandom(this.random.nextLong() ^ RandomChoices.hash(seed));
    }

    public long getSeed() {
        return this.seed;
    }

    public Random getRandom() {
        return this.random;
    }
}
