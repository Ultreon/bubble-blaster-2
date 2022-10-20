package com.ultreon.bubbles.render;

public class ValueAnimator {
    // IntegerRange.
    private final double valueStart;
    private final double valueEnd;
    private final double duration;

    // Flags
    private boolean active;
    private double timeStart;
    private double timeEnd;

    public ValueAnimator(double start, double end, double duration) {
        this.valueStart = start;
        this.valueEnd = end;
        this.duration = duration;
    }

    public void start() {
        this.active = true;
        this.timeStart = (double) System.nanoTime() / 1000000000d;
        this.timeEnd = timeStart + duration;
    }

    public double animate() {
        if (active) {
            double currentTime = (double) System.nanoTime() / 1000000000d;

            double now;
            if (timeEnd - timeStart != 0) {
                now = (((currentTime - timeEnd) / (timeEnd - timeStart)) + 1) / 2;
            } else {
                now = 1;
            }

            if (now >= 1) now = 1;
            else if (now <= 0) now = 0;

            return (now * (valueEnd - valueStart)) + valueStart;
        }
        return 0;
    }

    public boolean isEnded() {
        double currentTime = (double) System.nanoTime() / 1000000000d;
        double now = (((currentTime - timeEnd) / (timeEnd - timeStart)) + 1) / 2;

        return now >= 1;
    }
}
