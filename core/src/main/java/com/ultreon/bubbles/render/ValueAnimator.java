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
        this.timeEnd = this.timeStart + this.duration;
    }

    public double animate() {
        if (this.active) {
            var currentTime = (double) System.nanoTime() / 1000000000d;

            double now;
            if (this.timeEnd - this.timeStart != 0) {
                now = (((currentTime - this.timeEnd) / (this.timeEnd - this.timeStart)) + 1) / 2;
            } else {
                now = 1;
            }

            if (now >= 1) now = 1;
            else if (now <= 0) now = 0;

            return (now * (this.valueEnd - this.valueStart)) + this.valueStart;
        }
        return 0;
    }

    public boolean isEnded() {
        var currentTime = (double) System.nanoTime() / 1000000000d;
        var now = (((currentTime - this.timeEnd) / (this.timeEnd - this.timeStart)) + 1) / 2;

        return now >= 1;
    }
}
