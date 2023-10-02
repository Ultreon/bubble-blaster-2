package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.util.RandomValueSource;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

public class BubbleFreezeBubble extends BubbleType {
    public BubbleFreezeBubble() {
        this.setPriority(72_750L);
        this.setRadius(RandomValueSource.random(17, 58));
        this.setSpeed(RandomValueSource.random(4.115d, 6.845d));
        this.setScore(RandomValueSource.random(0.8, 1.4));
        this.setColors("#ff0000,#ff7f00,#ffff00,#ffff7f,#ffffff");
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        source.getWorld().freezeBubbles((int) (source.getRadius() * 1.6f) * TPS / 2);
    }
}
