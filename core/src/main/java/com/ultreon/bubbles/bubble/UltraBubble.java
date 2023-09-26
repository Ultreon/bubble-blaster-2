package com.ultreon.bubbles.bubble;

import com.jhlabs.image.HSBAdjustFilter;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.init.StatusEffects;
import org.apache.commons.lang3.Range;

import java.util.ArrayList;

public class UltraBubble extends BubbleType {
    public UltraBubble() {
        setColors("#007fff,#0000ff,#7f00ff,#ff00ff,#ff007f");

        setPriority(4600d);
        setRadius(Range.between(21, 55));
        setSpeed(Range.between(19.2, 38.4));
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
            player.addEffect(new StatusEffectInstance(StatusEffects.ATTACK_BOOST, 10, 3));
            player.addEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 3));
            player.addEffect(new StatusEffectInstance(StatusEffects.SCORE, 12, 10));
            player.addEffect(new StatusEffectInstance(StatusEffects.BUBBLE_FREEZE, 8, 1));
            player.addEffect(new StatusEffectInstance(StatusEffects.LUCK, 8, 1));
        }
    }
}
