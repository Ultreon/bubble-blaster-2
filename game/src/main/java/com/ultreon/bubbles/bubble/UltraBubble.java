package com.ultreon.bubbles.bubble;

import com.jhlabs.image.HSBAdjustFilter;
import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.init.Effects;
import com.ultreon.commons.util.ColorUtils;
import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.lang.math.IntRange;

import java.util.ArrayList;

public class UltraBubble extends BubbleType {
    public UltraBubble() {
        colors = ColorUtils.parseColorString("#007fff,#0000ff,#7f00ff,#ff00ff,#ff007f");

        setPriority(4600d);
        setRadius(new IntRange(21, 55));
        setSpeed(new DoubleRange(19.2, 38.4));
        setDefense(0.573f);
        setAttack(0.0f);
        setScore(10);
        setHardness(1.0d);
    }

    @Override
    public ArrayList<Object> getFilters(Bubble bubble) {
        ArrayList<Object> filters = new ArrayList<>();

        HSBAdjustFilter filter = new HSBAdjustFilter();
        filter.setHFactor((float) (System.currentTimeMillis() / 3) % 1);
        filters.add(filter);

        return filters;
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        super.onCollision(source, target);
        if (target instanceof Player player) {
            player.addEffect(new AppliedEffect(Effects.ATTACK_BOOST.get(), 10, 3));
            player.addEffect(new AppliedEffect(Effects.DEFENSE_BOOST.get(), 10, 3));
            player.addEffect(new AppliedEffect(Effects.MULTI_SCORE.get(), 12, 10));
            player.addEffect(new AppliedEffect(Effects.BUBBLE_FREEZE.get(), 8, 1));
            player.addEffect(new AppliedEffect(Effects.LUCK.get(), 8, 1));
        }
    }
}
