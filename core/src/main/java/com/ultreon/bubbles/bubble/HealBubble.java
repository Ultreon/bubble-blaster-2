package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import org.apache.commons.lang3.Range;

public class HealBubble extends BubbleType {
    public HealBubble() {
        setColors("#00c000,#00000000,#00c000,#00c000");

        setPriority(4000000);
        setRadius(Range.between(17, 70));
        setSpeed(Range.between(10.2d, 18.6d));
        setDefense(0.3f);
        setAttack(0.0f);
        setScore(1);
        setHardness(1.0d);
        setInvincible(true);
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        super.onCollision(source, target);

        if (target instanceof Player player) {
            float healAmount = 4.0f * (source.getEnvironment().getLocalDifficulty() / 20.0f + (1.8f / 20.0f));
            player.restoreDamage(healAmount);
            int newRad = (int)(source.getRadius() - healAmount / 1.1d);
            if (newRad < 5) source.pop();
            else source.setRadius(newRad);
        }
    }
}
