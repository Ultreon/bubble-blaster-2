package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import com.ultreon.bubbles.util.ConstantValueSource;
import com.ultreon.bubbles.util.RandomValueSource;

public class TripleStateBubble extends BubbleType {
    public TripleStateBubble() {
        this.setColors("#00ffff,#00ffff,#00000000,#00ffff,#00000000,#00ffff");

        this.setPriority(115000L);
        this.setRadius(RandomValueSource.random(21, 55));
        this.setSpeed(RandomValueSource.random(4.1, 10.4));
        this.setDefense(RandomValueSource.random(0.3f, 0.4f));
        this.setAttack(ConstantValueSource.of());
        this.setScore(ConstantValueSource.of(3));
        this.setHardness(ConstantValueSource.of(1));
    }

    @Override
    public StatusEffectInstance getEffect(Bubble source, Entity target) {
        return new StatusEffectInstance(StatusEffects.SCORE, source.getRadius() / 8, 3);
    }
}
