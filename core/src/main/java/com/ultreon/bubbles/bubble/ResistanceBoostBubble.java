package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import com.ultreon.bubbles.util.ConstantValueSource;
import com.ultreon.bubbles.util.RandomValueSource;

public class ResistanceBoostBubble extends BubbleType {
    public ResistanceBoostBubble() {
        this.setColors("00ffc0,40ffd0,80ffe0,c0fff0,ffffff");

        this.setPriority(131_072);
        this.setRadius(RandomValueSource.random(21, 70));
        this.setSpeed(RandomValueSource.random(8.8, 16.4));
        this.setDefense(RandomValueSource.random(0.1, 0.4));
        this.setAttack(ConstantValueSource.of());
        this.setScore(RandomValueSource.random(0.2, 1.0));
        this.setHardness(ConstantValueSource.of(1));
    }

    @Override
    public StatusEffectInstance getEffect(Bubble source, Entity target) {
        return new StatusEffectInstance(StatusEffects.RESISTANCE, source.getRadius() / 4, (byte) ((byte) source.getRadius() / 12 + 1));
    }
}
