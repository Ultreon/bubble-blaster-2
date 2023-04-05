package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import org.apache.commons.lang3.Range;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Levelup Bubble
 *
 * @author Qboi
 * @since 0.0.0
 */
public class LevelUpBubble extends BubbleType {
    public LevelUpBubble() {
        // Color & key.
        setColors("#ffff00,#ffffff,#ff9f00");

        // Set initial data values.
        setPriority(131_072L);
        setRadius(Range.between(21, 60));
        setSpeed(Range.between(6.4, 19.2));
        setDefense(Float.NaN);
        setAttack(0.0f);
        setScore(1);
        setHardness(1.0d);
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        super.onCollision(source, target);

        // Check target is a player/
        if (target instanceof Player player) {
            // Remove Bubble.
            source.delete();

            // Player level-up.
            player.levelUp();
        }
    }

    @Override
    public boolean canSpawn(@NonNull Environment environment) {
        // If player is not spawned yet, the player cannot have any change. So return false.
        if (environment.getPlayer() == null) return false;

        // Calculate the maximum level for the player's score.
        int maxLevelUp = (int) Math.round(environment.getPlayer().getScore()) / 50_000 + 1;

        // Check for existing level-up bubble entities.
        if (environment.getEntities().stream().
                filter((entity) -> entity instanceof Bubble) // Filter for bubble entities.
                .map((entity) -> (Bubble) entity) // Cast to bubble entities.
                .anyMatch(bubbleEntity -> bubbleEntity.getBubbleType() == this)) { // Check for level-up bubbles create the type create this class.
            return false; // Then it can't spawn.
        }

        // Return flag for ‘if the maximum level for score is greater than the player's current level’
        return maxLevelUp > environment.getPlayer().getLevel();
    }
}
