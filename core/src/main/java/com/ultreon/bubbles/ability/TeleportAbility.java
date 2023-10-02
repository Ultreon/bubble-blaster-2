package com.ultreon.bubbles.ability;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.ability.triggers.AbilityKeyTrigger;
import com.ultreon.bubbles.ability.triggers.types.AbilityKeyTriggerType;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.player.ability.Ability;
import com.ultreon.bubbles.entity.player.ability.AbilityTrigger;
import com.ultreon.bubbles.entity.player.ability.AbilityTriggerType;
import com.ultreon.bubbles.init.Abilities;
import com.ultreon.bubbles.util.helpers.MathHelper;

import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * Teleport ability.
 * 
 * @since 0.0.0
 * @author XyperCode
 * @see Abilities#TELEPORT_ABILITY
 */
public class TeleportAbility extends Ability<TeleportAbility> {
    /**
     * Constructor for the teleport ability.
     * 
     * @since 0.0.0
     * @author XyperCode
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
            deltaTime = MathHelper.clamp(deltaTime, 0, 2500);  // 0 to 2.5 seconds.

            // Motion.
            float deltaMotion = (float) deltaTime / 100 * ((float) deltaTime / 100);

            // Calculate position difference from the player's rotation and position.
            float angelRadians = player.getRotation() * MathUtils.degRad;
            float tempVelX = MathUtils.cos(angelRadians) * deltaMotion;
            float tempVelY = MathUtils.sin(angelRadians) * deltaMotion;

            Vector2 pos = new Vector2(player.getX() + tempVelX, player.getY() + tempVelY);

            // Teleport to that position.
            player.teleport(pos);

            // Use up the value, and set cooldown.
            this.useValue((int) deltaTime);
            this.setCooldown((int) (deltaTime / 3));
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
