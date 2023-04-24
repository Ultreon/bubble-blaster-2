package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import org.apache.commons.lang3.Range;

public class AttackBoostBubble extends BubbleType {
    public AttackBoostBubble() {
        setColors("804020,a07e5c,c0bc98,e0d8d0,ffffff");

        setPriority(98_304);
        setRadius(Range.between(21, 70));
        setSpeed(Range.between(8.4, 15.6));
        setDefense(0.0775f);
        setAttack(0.0f);
        setScore(2);
        setHardness(1.0d);
    }

    @Override
    public AppliedEffect getEffect(Bubble source, Entity target) {
        return new AppliedEffect(StatusEffects.ATTACK_BOOST, source.getRadius() / 8, source.getRadius() / 24 + 1);
    }
}
