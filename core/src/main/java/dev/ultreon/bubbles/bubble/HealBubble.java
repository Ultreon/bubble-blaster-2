package dev.ultreon.bubbles.bubble;

import dev.ultreon.bubbles.BubbleBlasterConfig;
import dev.ultreon.bubbles.entity.Bubble;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.attribute.Attribute;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.random.valuesource.ConstantValueSource;
import dev.ultreon.bubbles.random.valuesource.RandomValueSource;

public class HealBubble extends BubbleType {
    public HealBubble() {
        this.setColors("#00c000,#00000000,#00c000,#00c000");

        this.setPriority(1_000_000);
        this.setRadius(RandomValueSource.random(17, 70));
        this.setSpeed(RandomValueSource.random(10.2d, 18.6d));
        this.setDefense(RandomValueSource.random(2, 6));
        this.setAttack(ConstantValueSource.of(0.0));
        this.setScore(RandomValueSource.random(0.0, 0.5));
        this.setHardness(RandomValueSource.random(0.0, 0.3));
        this.setInvincible(true);
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        super.onCollision(source, target);

        if (target instanceof Player) {
            var player = (Player) target;
            var healAmount = source.getWorld().getLocalDifficulty() / 20.0f + 1.8f / 20.0f;
            healAmount /= (float) source.getAttributes().get(Attribute.DEFENSE);
            player.restoreDamage(healAmount);

            // Set ra
            var newRad = source.getRadius() - healAmount * 20f;
            if (newRad < 2 * this.getColors().size() * BubbleBlasterConfig.BUBBLE_LINE_THICKNESS.get()) source.pop();
            else source.setRadius(newRad);
        }
    }
}
