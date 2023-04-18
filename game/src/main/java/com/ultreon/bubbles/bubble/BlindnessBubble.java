package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import org.apache.commons.lang3.Range;

public class BlindnessBubble extends BubbleType {
    public BlindnessBubble() {
        setColors("000000,202020,404040,606060,808080");

        setPriority(640_000L);
        setRadius(Range.between(21, 70));
        setSpeed(Range.between(7.4, 12.6));
        setDefense(0.2369f);
        setAttack(0.0f);
        setScore(2);
        setHardness(1.0d);
    }

    @Override
    public AppliedEffect getEffect(Bubble source, Entity target) {
        return new AppliedEffect(StatusEffects.BLINDNESS, source.getRadius() / 8, (byte) ((byte) source.getRadius() / 24 + 1));
    }
}
