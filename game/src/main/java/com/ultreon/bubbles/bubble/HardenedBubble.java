package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.commons.util.ColorUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Range;

public class HardenedBubble extends BubbleType {
    public HardenedBubble() {
        colors = ColorUtils.parseColorString("#000000,#4f4f4f,#ff7f00,#ffff00");

        setPriority(387_500L);
        setRadius(Range.between(21, 60));
        setSpeed(Range.between(4.5, 7.0));
        setAttack(0.0f);
        setScore(1f);
        setHardness(1.0d);

//        BubbleInit.BUBBLES.add(this);
    }

    @Override
    public float getDefense(Environment environment, Rng rng) {
        float val = environment.getLocalDifficulty() * 4;
        return rng.getNumber(val / 4f, 3f * val / 4f, environment.getTicks(), 1L);
    }

    @Override
    public boolean isDefenseRandom() {
        return true;
    }
}
