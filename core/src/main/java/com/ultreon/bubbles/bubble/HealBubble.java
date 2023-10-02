package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.util.RandomValueSource;

public class HealBubble extends BubbleType {
    public HealBubble() {
        this.setColors("#00c000,#00000000,#00c000,#00c000");

        this.setPriority(4000000);
        this.setRadius(RandomValueSource.random(17, 70));
        this.setSpeed(RandomValueSource.random(10.2d, 18.6d));
        this.setDefense(RandomValueSource.random(2, 6));
        this.setAttack(RandomValueSource.random(0.0, 0.1));
        this.setScore(RandomValueSource.random(0.0, 0.5));
        this.setHardness(RandomValueSource.random(0.0, 0.3));
        this.setInvincible(true);
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        super.onCollision(source, target);

        if (target instanceof Player player) {
            var healAmount = source.getWorld().getLocalDifficulty() / 20.0f + 1.8f / 20.0f;
            healAmount /= (float) source.getAttributes().get(Attribute.DEFENSE);
            player.restoreDamage(healAmount);

            // Set ra
            var newRad = source.getRadius() - healAmount / 1.1f;
            if (newRad < 2 * this.getColors().size() * BubbleBlasterConfig.BUBBLE_LINE_THICKNESS.get()) source.pop();
            else source.setRadius(newRad);
        }
    }
}
