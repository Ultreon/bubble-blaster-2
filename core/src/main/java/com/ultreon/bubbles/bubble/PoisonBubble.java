package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.ai.AiAttack;
import com.ultreon.bubbles.entity.ai.AiTarget;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.random.valuesource.ConstantValueSource;
import com.ultreon.bubbles.random.valuesource.RandomValueSource;

public class PoisonBubble extends BubbleType {
    public PoisonBubble() {
        this.setColors("#7fff00,#9faf1f,#bf7f3f,#df3f5f,#ff007f");

        this.setPriority(1_550_000L);
        this.setRadius(RandomValueSource.random(34, 83));
        this.setSpeed(RandomValueSource.random(8.0d, 14.0d));
        this.setDefense(RandomValueSource.random(0.15, 0.3));
        this.setAttack(RandomValueSource.random(0.5, 1.0));
        this.setScore(RandomValueSource.random(0.3, 0.4));
        this.setHardness(ConstantValueSource.of(1.0));

        this.addAiTask(0, new AiAttack());
        this.addAiTask(1, new AiTarget(Entities.PLAYER));
    }

    @Override
    public double getModifiedPriority(double localDifficulty) {
        return this.getPriority() * localDifficulty / 15d;
    }
}
