package com.ultreon.bubbles.ability;

import com.ultreon.bubbles.ability.triggers.AbilityKeyTrigger;
import com.ultreon.bubbles.ability.triggers.types.AbilityKeyTriggerType;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.player.ability.Ability;
import com.ultreon.bubbles.entity.player.ability.AbilityTrigger;
import com.ultreon.bubbles.entity.player.ability.AbilityTriggerType;
import com.ultreon.bubbles.init.Abilities;
import com.ultreon.bubbles.util.helpers.Mth;

import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.Objects;

/**
 * Teleport ability.
 * 
 * @since 0.0.0
 * @author Qboi123
 * @see Abilities#TELEPORT_ABILITY
 */
public class TeleportAbility extends Ability<TeleportAbility> {
    /**
     * Constructor for the teleport ability.
     * 
     * @since 0.0.0
     * @author Qboi123
     */
    public TeleportAbility() {
        super(Objects.requireNonNull(Abilities.TELEPORT_ABILITY));
    }

    @Override
    public int getTriggerKey() {
        return KeyEvent.VK_SHIFT;
    }

    @Override
    public AbilityTriggerType getTriggerType() {
        return AbilityTriggerType.KEY_TRIGGER;
    }

    @Override
    public AbilityKeyTriggerType getKeyTriggerType() {
        return AbilityKeyTriggerType.HOLD;
    }

    @Override
    public void trigger(AbilityTrigger trigger) {
        Entity entity = trigger.getEntity();

        // Check for player entity.
        if (entity instanceof Player player) {
            // Calculate delta time
            long startTime = player.getTag().getLong("TeleportAbilityStartTime");
            player.getTag().remove("TeleportAbilityStartTime");

            long deltaTime = System.currentTimeMillis() - startTime;
            deltaTime = Mth.clamp(deltaTime, 0, 2500);  // 0 to 2.5 seconds.

            // Motion.
            double deltaMotion = Math.pow((double) deltaTime / 100, 2);

            // Calculate position difference from the player's rotation and position.
            double angelRadians = Math.toRadians(player.getRotation());
            double tempVelX = Math.cos(angelRadians) * deltaMotion;
            double tempVelY = Math.sin(angelRadians) * deltaMotion;

            Point2D pos = new Point2D.Double(player.getX() + tempVelX, player.getY() + tempVelY);

            // Teleport to that position.
            player.teleport(pos);

            // Use up the value, and set cooldown.
            useValue((int) deltaTime);
            setCooldown((int) (deltaTime / 3));
        }
    }

    @Override
    public void onKeyTrigger(AbilityKeyTrigger trigger) {
        Entity entity = trigger.getEntity();

        if (entity instanceof Player player) {
            player.getTag().putLong("TeleportAbilityStartTime", System.currentTimeMillis());
        }
    }

    @Override
    public void triggerEntity() {

    }

    @Override
    public boolean canBeTriggered(Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public boolean canRegenerate() {
        return true;
    }
}
