package dev.ultreon.test.bubbles;

import dev.ultreon.bubbles.render.ValueAnimator;

public class TestAnimation {
    public static void main(String[] args) {
        var valueAnimator = new ValueAnimator(100, 200, 30d);
        valueAnimator.start();

        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.println("Current value: " + valueAnimator.animate());
            try {
                //noinspection BusyWait
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
