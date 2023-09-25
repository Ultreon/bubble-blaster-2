package com.ultreon.bubbles.bubble;

import com.badlogic.gdx.math.MathUtils;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import org.apache.commons.lang3.Range;

public class AccelerateBubble extends BubbleType {
    public AccelerateBubble() {
        setColors("#00003f,#00007f,#0000af,#0000ff");
        setPriority(2440000);
        setRadius(Range.between(25, 54));
        setSpeed(Range.between(6.0, 28.0));
        setDefense(1.0f);
        setAttack(0.001f);
        setScore(1);
        setHardness(0.7d);
        setInvincible(true);
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        if (target instanceof Player player) {
            // Calculate Velocity X and Y.
            float accelerateX = 0;
            float accelerateY = 0;
            if (player.isMobile()) {
                accelerateX += MathUtils.cos(player.getRotation() * MathUtils.degRad) * 0.375f;
                accelerateY += MathUtils.sin(player.getRotation() * MathUtils.degRad) * 0.375f;
            }

            // Set velocity X and Y.
            player.setAccelerateX(player.getAccelerateX() + accelerateX);
            player.setAccelerateY(player.getAccelerateY() + accelerateY);
        }
    }
}
