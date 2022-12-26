package com.ultreon.test.bubbles;

import com.ultreon.bubbles.animation.Animation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnimationTest {
    @Test()
    @DisplayName("Upwards Test")
    @Timeout(12)
    void upwardsTest() {
        Animation animation = new Animation(0);
        long start = System.currentTimeMillis();
        animation.goTo(100, 10);
        double val;
        while ((val = animation.get()) != 100) {
            double finalVal = val;
            System.out.println("Value: " + val);
            assertTrue(() -> !Double.isInfinite(finalVal) && !Double.isNaN(finalVal), "Value " + val + " is invalid");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long end = System.currentTimeMillis();
        long duration = end - start;
        assertFalse(duration < 2000, "Took shorter that requested time");
        System.out.println("Value: " + animation.get());
    }

    @Test()
    @DisplayName("Downwards Test")
    @Timeout(12)
    void downwardsTest() {
        Animation animation = new Animation(100);
        long start = System.currentTimeMillis();
        animation.goTo(0, 10);
        double val;
        while ((val = animation.get()) != 0) {
            double finalVal = val;
            System.out.println("Value: " + val);
            assertTrue(() -> !Double.isInfinite(finalVal) && !Double.isNaN(finalVal), "Value " + val + " is invalid");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long end = System.currentTimeMillis();
        long duration = end - start;
        assertFalse(duration < 2000, "Took shorter that requested time");
        System.out.println("Value: " + animation.get());
    }
}
