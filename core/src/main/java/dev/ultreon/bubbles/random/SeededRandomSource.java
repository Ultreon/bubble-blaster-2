package dev.ultreon.bubbles.random;

public interface SeededRandomSource extends RandomSource {
    long getSeed();
}
