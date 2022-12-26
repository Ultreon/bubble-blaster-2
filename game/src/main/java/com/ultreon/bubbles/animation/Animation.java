package com.ultreon.bubbles.animation;

public class Animation {
    private double start;
    private double percentage;
    private double end;
    private double seconds;
    private double startTime;
    private boolean initialized;

    public Animation(double start) {
        this.start = start;
        this.end = start;
    }

    public void goTo(double value, double seconds) {
        if (this.initialized) {
            this.start = get();
        }
        this.initialized = true;
        this.end = value;
        this.seconds = seconds;
        this.startTime = System.currentTimeMillis() / 1000.0;
    }

    private void update() {
        var time = System.currentTimeMillis() / 1000.0;
        var diff = time - startTime;
        if (diff > seconds) diff = seconds;
        if (seconds <= 0) {
            percentage = 1;
            return;
        }
        percentage = Math.min(Math.max(1 * diff / seconds, 0), 1);
    }

    public double get() {
        update();
        return lerp(percentage, start, end);
    }

    private double lerp(double amt, double start, double end) {
        if (amt <= 0.0D) return start;
        if (amt >= 1.0D) return end;
        return start + (end - start) * amt;
    }
}
