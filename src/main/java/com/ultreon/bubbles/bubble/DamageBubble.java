package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.ai.AiAttack;
import com.ultreon.bubbles.entity.ai.AiTarget;
import com.ultreon.bubbles.init.Entities;
import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.lang.math.IntRange;

import java.awt.*;

public class DamageBubble extends BubbleType {
    public DamageBubble() {
        colors = new Color[]{new Color(255, 0, 0), new Color(255, 96, 0), new Color(255, 0, 0)};

        setPriority(20_000_000d);
        setRadius(new IntRange(17, 70));
        setSpeed(new DoubleRange(12.0d, 20.0d));
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
