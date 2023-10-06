package com.ultreon.bubbles.bubble;

import com.badlogic.gdx.graphics.Texture;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.random.valuesource.ConstantValueSource;
import com.ultreon.bubbles.random.valuesource.RandomValueSource;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Level-up Bubble
 *
 * @author XyperCode
 * @since 0.0.0
 */
public class LevelUpBubble extends BubbleType {
    public LevelUpBubble() {
        // Color & key.
        this.setColors("#ffff00,#ffffff,#ff9f00");

        // Set initial data values.
        this.setPriority(131_072L);
        this.setRadius(RandomValueSource.random(65, 100));
        this.setSpeed(RandomValueSource.random(6.4, 19.2));
        this.setDefense(ConstantValueSource.of(Float.NaN));
        this.setAttack(ConstantValueSource.of());
        this.setScore(RandomValueSource.random(1.0, 2.5));
        this.setHardness(ConstantValueSource.of(Float.MIN_NORMAL));
    }

    @Override
    public void onCollision(Bubble source, Entity target) {
        super.onCollision(source, target);

        // Check target is a player/
        if (target instanceof Player) {
            Player player = (Player) target;
            // Remove Bubble.
            source.delete();

            // Player level-up.
            player.levelUp();
        }
    }

    @Override
    public boolean canSpawn(@NotNull World world) {
        // If player is not spawned yet, the player cannot have any change. So return false.
        if (world.getPlayer() == null) return false;

        // Calculate the maximum level for the player's score.
        int maxLevelUp = (int) Math.round(world.getPlayer().getScore()) / BubbleBlasterConfig.LEVEL_THRESHOLD.get() + 1;

        // Check for existing level-up bubble entities.
        if (world.getEntities().stream().
                filter((entity) -> entity instanceof Bubble) // Filter for bubble entities.
                .map((entity) -> (Bubble) entity) // Cast to bubble entities.
                .anyMatch(bubbleEntity -> bubbleEntity.getBubbleType() == this)) { // Check for level-up bubbles create the type create this class.
            return false; // Then it can't spawn.
        }

        // Return flag for ‘if the maximum level for score is greater than the player's current level’
        return maxLevelUp > world.getPlayer().getLevel();
    }

    @Override
    public Texture getInsideTexture() {
        return BubbleBlaster.getInstance().getTextureManager().getOrLoadTexture(new Identifier("bubble/key"));
    }
}
