package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import org.apache.commons.lang3.Range;

public class DoubleStateBubble extends BubbleType {
    public DoubleStateBubble() {
        setColors("#ffc000,#ffc000,#00000000,#ffc000");

        setPriority(460000L);
        setRadius(Range.between(21, 55));
        setSpeed(Range.between(4.0, 10.8));
        setDefense(0.22f);
        setAttack(0.0f);
        setScore(2);
        setHardness(1.0d);
    }

    @Override
    public AppliedEffect getEffect(Bubble source, Entity target) {
        return new AppliedEffect(StatusEffects.MULTI_SCORE, source.getRadius() / 8, 2);
    }
}
