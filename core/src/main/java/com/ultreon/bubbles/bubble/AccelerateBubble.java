package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.util.ConstantValueSource;
import com.ultreon.bubbles.util.RandomValueSource;

public class AccelerateBubble extends BubbleType {
    public AccelerateBubble() {
        this.setColors("#00003f,#00007f,#0000af,#0000ff");
        this.setPriority(2440000);
        this.setRadius(RandomValueSource.random(25, 54));
        this.setSpeed(RandomValueSource.random(6.0, 28.0));
        this.setDefense(RandomValueSource.random(0.8, 1.2));
        this.setAttack(RandomValueSource.random(0.001, 0.05));
        this.setScore(ConstantValueSource.of(1));
        this.setHardness(RandomValueSource.random(0.6, 0.9));
        this.setInvincible(true);
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        if (target instanceof Player player) {
            player.boost(true);
        }
    }
}
