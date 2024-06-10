package dev.ultreon.bubbles.bubble;

import dev.ultreon.bubbles.data.DataKeys;
import dev.ultreon.bubbles.effect.StatusEffectInstance;
import dev.ultreon.bubbles.entity.Bubble;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.init.StatusEffects;
import dev.ultreon.bubbles.random.valuesource.RandomValueSource;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.libs.datetime.v0.Duration;

import static dev.ultreon.bubbles.BubbleBlaster.NAMESPACE;

public class BubbleFreezeBubble extends BubbleType {
    public BubbleFreezeBubble() {
        this.setPriority(72_750L);
        this.setRadius(RandomValueSource.random(17, 58));
        this.setSpeed(RandomValueSource.random(4.115d, 6.845d));
        this.setScore(RandomValueSource.random(0.8, 1.4));
        this.setColors("#ff0000,#ff7f00,#ffff00,#ffff7f,#ffffff");
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        target.addEffect(new StatusEffectInstance(StatusEffects.BUBBLE_FREEZE, Duration.ofSeconds((long) (source.getRadius() / 8)), 1));
    }

    @Override
    public double getModifiedPriority(double localDifficulty, World world) {
        double modifiedPriority = super.getModifiedPriority(localDifficulty, world);
        if (world.getGameplayStorage().get(NAMESPACE).getBoolean(DataKeys.GOLDEN_SPAWN_ACTIVE))
            modifiedPriority *= 4.0;

        return modifiedPriority;
    }
}
