package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import com.ultreon.bubbles.random.valuesource.RandomValueSource;
import com.ultreon.libs.datetime.v0.Duration;

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
        target.addEffect(new StatusEffectInstance(StatusEffects.BUBBLE_FREEZE, Duration.ofSeconds((long) (source.getRadius() / 8)), 1));
    }
}
