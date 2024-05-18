package dev.ultreon.bubbles.bubble;

import dev.ultreon.bubbles.effect.StatusEffectInstance;
import dev.ultreon.bubbles.entity.Bubble;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.init.StatusEffects;
import dev.ultreon.bubbles.random.valuesource.ConstantValueSource;
import dev.ultreon.bubbles.random.valuesource.RandomValueSource;
import dev.ultreon.libs.datetime.v0.Duration;

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
        var filters = new ArrayList<>();

//        HSBAdjustFilter filter = new HSBAdjustFilter();
//        filter.setHFactor((float) (System.currentTimeMillis() / 3) % 1);
//        filters.add(filter);

        return filters;
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        super.onCollision(source, target);
        if (target instanceof Player) {
            var player = (Player) target;
            player.addEffect(new StatusEffectInstance(StatusEffects.ATTACK_BOOST, Duration.ofSeconds(10), 10));
            player.addEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Duration.ofSeconds(10), 10));
            player.addEffect(new StatusEffectInstance(StatusEffects.SCORE, Duration.ofSeconds(12), 10));
            player.addEffect(new StatusEffectInstance(StatusEffects.LUCK, Duration.ofSeconds(8), 5));
            player.addEffect(new StatusEffectInstance(StatusEffects.SWIFTNESS, Duration.ofSeconds(10), 8));
            player.addEffect(new StatusEffectInstance(StatusEffects.INVINCIBILITY, Duration.ofSeconds(10), 8));

            player.getWorld().freezeBubblesSecs(8);
        }
    }
}
