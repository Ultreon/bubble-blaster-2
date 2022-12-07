package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.Effects;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Range;

import java.awt.*;

public class TripleStateBubble extends BubbleType {
//    public Color[] colors;

    public TripleStateBubble() {
//        colors = ColorUtils.multiConvertHexToRgb("#ffffff");
        colors = new Color[]{new Color(0, 255, 255), new Color(0, 255, 255), new Color(0, 0, 0, 0), new Color(0, 255, 255), new Color(0, 0, 0, 0), new Color(0, 255, 255)};

        setPriority(115000L);
        setRadius(Range.between(21, 55));
        setSpeed(Range.between(4.1, 10.4));
        setDefense(0.335f);
        setAttack(0.0f);
        setScore(3);
        setHardness(1.0d);
    }

    @Override
    public AppliedEffect getEffect(Bubble source, Entity target) {
        return new AppliedEffect(Effects.MULTI_SCORE.get(), source.getRadius() / 8, 3);
    }
}
