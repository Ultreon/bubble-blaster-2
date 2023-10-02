package com.ultreon.bubbles.random;

import com.ultreon.bubbles.util.RngUtils;
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
        this(RngUtils.hash(System.nanoTime()));
    }

    @Override
    public long nextLong() {
        return this.random.nextLong();
    }

    @Override
    public long nextLong(long max) {
        return this.random.nextLong(max);
    }

    @Override
    public long nextLong(long min, long max) {
        return this.random.nextLong(max, max);
    }

    @Override
    public int nextInt() {
        return this.random.nextInt();
    }

    @Override
    public int nextInt(int max) {
        return this.random.nextInt(max);
    }

    @Override
    public int nextInt(int min, int max) {
        return this.random.nextInt(min, max);
    }

    @Override
    public float nextFloat() {
        return this.random.nextFloat();
    }

    @Override
    public float nextFloat(float max) {
        return this.random.nextFloat(max);
    }

    @Override
    public float nextFloat(float min, float max) {
        return this.random.nextFloat(min, max);
    }

    @Override
    public double nextDouble() {
        return this.random.nextDouble();
    }

    @Override
    public double nextDouble(double max) {
        return this.random.nextDouble(max);
    }

    @Override
    public double nextDouble(double min, double max) {
        return this.random.nextDouble(min, max);
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
        return new JavaRandom(this.random.nextLong() ^ RngUtils.hash(seed));
    }

    @Override
    public RandomSource nextRandom(Identifier seed) {
        return new JavaRandom(this.random.nextLong() ^ RngUtils.hash(seed));
    }

    public long getSeed() {
        return this.seed;
    }

    public Random getRandom() {
        return this.random;
    }
}
