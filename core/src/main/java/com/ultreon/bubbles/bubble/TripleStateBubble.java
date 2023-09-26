package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import org.apache.commons.lang3.Range;

public class TripleStateBubble extends BubbleType {
    public TripleStateBubble() {
        setColors("#00ffff,#00ffff,#00000000,#00ffff,#00000000,#00ffff");

        setPriority(115000L);
        setRadius(Range.between(21, 55));
        setSpeed(Range.between(4.1, 10.4));
        setDefense(0.335f);
        setAttack(0.0f);
        setScore(3);
        setHardness(1.0d);
    }

    @Override
    public StatusEffectInstance getEffect(Bubble source, Entity target) {
        return new StatusEffectInstance(StatusEffects.SCORE, source.getRadius() / 8, 3);
    }
}
