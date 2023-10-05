package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import com.ultreon.bubbles.random.valuesource.ConstantValueSource;
import com.ultreon.bubbles.random.valuesource.RandomValueSource;

import java.time.Duration;

public class AttackBoostBubble extends BubbleType {
    public AttackBoostBubble() {
        this.setColors("804020,a07e5c,c0bc98,e0d8d0,ffffff");

        this.setPriority(98_304);
        this.setRadius(RandomValueSource.random(21, 70));
        this.setSpeed(RandomValueSource.random(8.4, 15.6));
        this.setDefense(RandomValueSource.random(0.075, 0.15));
        this.setAttack(ConstantValueSource.of());
        this.setScore(RandomValueSource.random(1, 2));
        this.setHardness(ConstantValueSource.of(1));
    }

    @Override
    public StatusEffectInstance getEffect(Bubble source, Entity target) {
        return new StatusEffectInstance(StatusEffects.ATTACK_BOOST, Duration.ofSeconds((long) (source.getRadius() / 8)), (int) (source.getRadius() / 12 + 1));
    }
}
