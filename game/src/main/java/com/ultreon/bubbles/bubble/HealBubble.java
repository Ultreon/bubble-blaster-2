package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.commons.util.ColorUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Range;

import java.awt.*;

public class HealBubble extends BubbleType {
//    public Color[] colors;

    public HealBubble() {
        colors = ColorUtils.multiConvertHexToRgb("#ffffff");
        colors = new Color[]{new Color(0, 192, 0), new Color(0, 0, 0, 0), new Color(0, 192, 0), new Color(0, 192, 0)};

        setPriority(4000000);
        setRadius(Range.between(17, 70));
        setSpeed(Range.between(10.2d, 18.6d));
        setDefense(0.3f);
        setAttack(0.0f);
        setScore(1);
        setHardness(1.0d);

//        BubbleInit.BUBBLES.add(this);
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        super.onCollision(source, target);

        if (target instanceof Player player) {
            player.restoreDamage(4.0f * (source.getEnvironment().getLocalDifficulty() / 20.0f + (1.8f / 20.0f)));
        }
    }
}
