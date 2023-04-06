package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.Effects;
import org.apache.commons.lang3.Range;

public class SpeedBoostBubble extends BubbleType {
    public SpeedBoostBubble() {
        setColors("#0080ff,#40a0ff,#80c0ff,#c0e0ff,#ffffff");

        setPriority(460000L);
        setRadius(Range.between(21, 55));
        setSpeed(Range.between(16.2, 24.8));
        setDefense(0.1f);
        setAttack(0.0f);
        setScore(0);
        setHardness(1.0d);
    }

    @Override
    public AppliedEffect getEffect(Bubble source, Entity target) {
//        return new AppliedEffect(Effects.SPEED_BOOST.get(), source.getRadius() / 8, (byte) (source.getSpeed() / 3.2d));
        return null;
    }
}
