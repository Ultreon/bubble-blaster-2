package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.ai.AiAttack;
import com.ultreon.bubbles.entity.ai.AiTarget;
import com.ultreon.bubbles.init.Entities;
import org.apache.commons.lang3.Range;

public class PoisonBubble extends BubbleType {
    public PoisonBubble() {
        setColors("#7fff00,#9faf1f,#bf7f3f,#df3f5f,#ff007f");

        setPriority(1_550_000L);
        setRadius(Range.between(34, 83));
        setSpeed(Range.between(8.0d, 14.0d));
        setDefense(0.225f);
        setAttack(0.5f);
        setScore(0.375f);
        setHardness(1.0d);

        addAiTask(0, new AiAttack());
        addAiTask(1, new AiTarget(Entities.PLAYER));
    }

    @Override
    public double getModifiedPriority(double localDifficulty) {
        return getPriority() * localDifficulty / 15d;
    }
}
