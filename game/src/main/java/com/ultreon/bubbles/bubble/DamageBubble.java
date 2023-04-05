package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.ai.AiAttack;
import com.ultreon.bubbles.entity.ai.AiTarget;
import com.ultreon.bubbles.init.Entities;
import org.apache.commons.lang3.Range;

public class DamageBubble extends BubbleType {
    public DamageBubble() {
        setColors("ff0000,ff6000,ff0000");

        setPriority(20_000_000d);
        setRadius(Range.between(17, 70));
        setSpeed(Range.between(12.0d, 20.0d));
        setDefense(0.2f);
        setAttack(1.5f);
        setScore(1);
        setHardness(1.0d);

        addAiTask(0, new AiAttack());
        addAiTask(1, new AiTarget(Entities.PLAYER.get()));
    }

    @Override
    public double getModifiedPriority(double localDifficulty) {
        return getPriority() * localDifficulty / 10d;
    }
}
