package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.init.StatusEffects;
import com.ultreon.bubbles.random.valuesource.ConstantValueSource;
import com.ultreon.bubbles.random.valuesource.RandomValueSource;

import java.util.ArrayList;

public class UltraBubble extends BubbleType {
    public UltraBubble() {
        this.setColors("#007fff,#0000ff,#7f00ff,#ff00ff,#ff007f");

        this.setPriority(4600d);
        this.setRadius(RandomValueSource.random(21, 55));
        this.setSpeed(RandomValueSource.random(19.2, 38.4));
        this.setDefense(RandomValueSource.random(0.5, 0.7));
        this.setAttack(ConstantValueSource.of());
        this.setScore(ConstantValueSource.of(10));
        this.setHardness(RandomValueSource.random(1, 4));
    }

    @Override
    public ArrayList<Object> getFilters(Bubble bubble) {
        ArrayList<Object> filters = new ArrayList<>();

//        HSBAdjustFilter filter = new HSBAdjustFilter();
//        filter.setHFactor((float) (System.currentTimeMillis() / 3) % 1);
//        filters.add(filter);

        return filters;
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        super.onCollision(source, target);
        if (target instanceof Player player) {
            player.addEffect(new StatusEffectInstance(StatusEffects.ATTACK_BOOST, 10, 3));
            player.addEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 3));
            player.addEffect(new StatusEffectInstance(StatusEffects.SCORE, 12, 10));
            player.addEffect(new StatusEffectInstance(StatusEffects.LUCK, 8, 1));

            player.getWorld().freezeBubblesSecs(8);
        }
    }
}
