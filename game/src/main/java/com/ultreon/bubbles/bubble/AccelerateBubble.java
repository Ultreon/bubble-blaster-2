package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.commons.util.ColorUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Range;

public class AccelerateBubble extends BubbleType {
    public AccelerateBubble() {
        colors = ColorUtils.parseColorString("#00003f,#00007f,#0000af,#0000ff");

//        BubbleInit.BUBBLES.add(this);

//        setPriority(244000);
        setPriority(2440000);
        setRadius(Range.between(25, 54));
        setSpeed(Range.between(6.0, 28.0));
        setDefense(1.0f);
        setAttack(0.001f);
        setScore(1);
        setHardness(0.7d);
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        if (target instanceof Player player) {
            // Calculate Velocity X and Y.
            double accelerateX = 0;
            double accelerateY = 0;
            if (player.isMobile()) {
                accelerateX += Math.cos(Math.toRadians(player.getRotation())) * 0.625d;
                accelerateY += Math.sin(Math.toRadians(player.getRotation())) * 0.625d;
            }

            // Set velocity X and Y.
            player.setAccelerateX(player.getAccelerateX() + accelerateX);
            player.setAccelerateY(player.getAccelerateY() + accelerateY);
        }
    }
}
