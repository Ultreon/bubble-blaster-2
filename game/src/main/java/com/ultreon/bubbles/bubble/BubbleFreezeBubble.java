package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.Effects;
import org.apache.commons.lang3.Range;

public class BubbleFreezeBubble extends BubbleType {
    public BubbleFreezeBubble() {
        setPriority(72_750L);
        setRadius(Range.between(17, 58));
        setSpeed(Range.between(4.115d, 6.845d));
        setScore(1.3125f);
        setEffect((source, target) -> (new AppliedEffect(Effects.BUBBLE_FREEZE.get(), source.getRadius() / 8, (byte) ((byte) source.getSpeed() * 4))));
        setColors("#ff0000,#ff7f00,#ffff00,#ffff7f,#ffffff");
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        source.getEnvironment().triggerBubbleFreeze((int) (source.getRadius() * 1.6f));
    }
}
