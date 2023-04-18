package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import org.apache.commons.lang3.Range;

public class DefenseBoostBubble extends BubbleType {
    public DefenseBoostBubble() {
        setColors("00ffc0,40ffd0,80ffe0,c0fff0,ffffff");

        setPriority(131_072);
        setRadius(Range.between(21, 70));
        setSpeed(Range.between(8.8, 16.4));
        setDefense(0.327f);
        setAttack(0.0f);
        setScore(2);
        setHardness(1.0d);
    }

    @Override
    public AppliedEffect getEffect(Bubble source, Entity target) {
        return new AppliedEffect(StatusEffects.DEFENSE_BOOST, source.getRadius() / 8, (byte) ((byte) source.getRadius() / 24 + 1));
    }
}
