package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.util.RandomValueSource;
import org.apache.commons.lang3.Range;

public class SpeedBoostBubble extends BubbleType {
    public SpeedBoostBubble() {
        this.setColors("#0080ff,#40a0ff,#80c0ff,#c0e0ff,#ffffff");

        this.setPriority(460000L);
        this.setRadius(RandomValueSource.random(21, 55));
        this.setSpeed(RandomValueSource.random(16.2, 24.8));
        this.setDefense(RandomValueSource.random(0.1, 0.4));
        this.setAttack(RandomValueSource.random(0, 0.2));
        this.setScore(RandomValueSource.random(0, 0.8));
        this.setHardness(RandomValueSource.random(1.0, 2.0));
    }

    @Override
    public StatusEffectInstance getEffect(Bubble source, Entity target) {
//        return new AppliedEffect(Effects.SPEED_BOOST.get(), source.getRadius() / 8, (byte) (source.getSpeed() / 3.2d));
        return null;
    }
}
