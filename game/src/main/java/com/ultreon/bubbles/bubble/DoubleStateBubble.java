package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.Effects;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Range;

import java.awt.*;

public class DoubleStateBubble extends BubbleType {
//    public Color[] colors;

    public DoubleStateBubble() {
//        colors = ColorUtils.multiConvertHexToRgb("#ffffff");
        colors = new Color[]{new Color(255, 192, 0), new Color(255, 192, 0), new Color(0, 0, 0, 0), new Color(255, 192, 0)};

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
        return new AppliedEffect(Effects.MULTI_SCORE.get(), source.getRadius() / 8, 2);
    }
}
