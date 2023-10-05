package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.ai.AiAttack;
import com.ultreon.bubbles.entity.ai.AiTarget;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.random.valuesource.ConstantValueSource;
import com.ultreon.bubbles.random.valuesource.RandomValueSource;

public class DamageBubble extends BubbleType {
    public DamageBubble() {
        this.setColors("ff0000,ff6000,ff0000");

        this.setPriority(20_000_000d);
        this.setRadius(RandomValueSource.random(17, 70));
        this.setSpeed(RandomValueSource.random(12, 20));
        this.setDefense(RandomValueSource.random(0.5, 1));
        this.setAttack(RandomValueSource.random(1, 1.5));
        this.setScore(RandomValueSource.random(0.6, 1.1));
        this.setHardness(ConstantValueSource.of(1));

        this.addAiTask(0, new AiAttack());
        this.addAiTask(1, new AiTarget(Entities.PLAYER));
    }

    @Override
    public double getModifiedPriority(double localDifficulty) {
        return this.getPriority() * localDifficulty / 10d;
    }
}
