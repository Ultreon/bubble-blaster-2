package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.Effects;
import com.ultreon.commons.util.ColorUtils;
import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.lang.math.IntRange;

public class BubbleFreezeBubble extends BubbleType {
    public BubbleFreezeBubble() {
        setPriority(72_750L);
        setRadius(new IntRange(17, 58));
        setSpeed(new DoubleRange(4.115d, 6.845d));
        setScore(1.3125f);
        setEffect((source, target) -> (new AppliedEffect(Effects.BUBBLE_FREEZE.get(), source.getRadius() / 8, (byte) ((byte) source.getSpeed() * 4))));
        setColors(ColorUtils.parseColorString("#ff0000,#ff7f00,#ffff00,#ffff7f,#ffffff", false));
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        source.getEnvironment().triggerBubbleFreeze((int) (source.getRadius() * 1.6f));
    }
}
