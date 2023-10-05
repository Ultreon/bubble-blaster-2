package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import com.ultreon.bubbles.random.valuesource.ConstantValueSource;
import com.ultreon.bubbles.random.valuesource.RandomValueSource;

import java.time.Duration;

public class BlindnessBubble extends BubbleType {
    public BlindnessBubble() {
        this.setColors("000000,202020,404040,606060,808080");

        this.setPriority(640_000L);
        this.setRadius(RandomValueSource.random(21, 70));
        this.setSpeed(RandomValueSource.random(7.4, 12.6));
        this.setDefense(RandomValueSource.random(0.23, 0.24));
        this.setAttack(ConstantValueSource.of(0.0));
        this.setScore(ConstantValueSource.of());
        this.setHardness(ConstantValueSource.of(1.0));
    }

    @Override
    public StatusEffectInstance getEffect(Bubble source, Entity target) {
        return new StatusEffectInstance(StatusEffects.BLINDNESS, Duration.ofSeconds((long) (source.getRadius() / 4)), (byte) ((byte) source.getRadius() / 24 + 1));
    }
}
