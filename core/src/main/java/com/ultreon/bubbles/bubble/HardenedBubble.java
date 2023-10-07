package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.random.valuesource.RandomValueSource;
import com.ultreon.bubbles.world.World;

public class HardenedBubble extends BubbleType {
    public HardenedBubble() {
        this.setColors("#000000,#4f4f4f,#ff7f00,#ffff00");

        this.setPriority(387_500L);
        this.setRadius(RandomValueSource.random(21, 60));
        this.setSpeed(RandomValueSource.random(4.5, 7));
        this.setAttack(RandomValueSource.random(1, 3));
        this.setScore(RandomValueSource.random(1, 2));
        this.setHardness(RandomValueSource.random(3, 8));
    }

    @Override
    public float getDefense(World world, Rng rng) {
        var val = world.getLocalDifficulty() * 4;
        return rng.getNumber(val / 4f, 3f * val / 4f, world.getTicks(), 1L);
    }

    @Override
    public boolean isDefenseRandom() {
        return true;
    }
}
