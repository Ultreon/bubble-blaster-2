package dev.ultreon.bubbles.ability;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import dev.ultreon.bubbles.ability.triggers.AbilityKeyTrigger;
import dev.ultreon.bubbles.ability.triggers.types.AbilityKeyTriggerType;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.entity.player.ability.Ability;
import dev.ultreon.bubbles.entity.player.ability.AbilityTrigger;
import dev.ultreon.bubbles.entity.player.ability.AbilityTriggerType;
import dev.ultreon.bubbles.init.Abilities;
import dev.ultreon.libs.commons.v0.Mth;

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
        var entity = trigger.getEntity();

        // Check for player entity.
        if (entity instanceof Player) {
            var player = (Player) entity;
            // Calculate delta time
            var startTime = player.getTag().getLong("TeleportAbilityStartTime");
            player.getTag().remove("TeleportAbilityStartTime");

            var deltaTime = System.currentTimeMillis() - startTime;
            deltaTime = Mth.clamp(deltaTime, 0, 2500);  // 0 to 2.5 seconds.

            // Motion.
            var deltaMotion = (float) deltaTime / 100 * ((float) deltaTime / 100);

            // Calculate position difference from the player's rotation and position.
            var angelRadians = player.getRotation() * MathUtils.degRad;
            var tempVelX = MathUtils.cos(angelRadians) * deltaMotion;
            var tempVelY = MathUtils.sin(angelRadians) * deltaMotion;

            var pos = new Vector2(player.getX() + tempVelX, player.getY() + tempVelY);

            // Teleport to that position.
            player.teleport(pos);

            // Use up the value, and set cooldown.
            this.useValue((int) deltaTime);
            this.setCooldown((int) (deltaTime / 3));
        }
    }

    @Override
    public void onKeyTrigger(AbilityKeyTrigger trigger) {
        var entity = trigger.getEntity();

        if (entity instanceof Player) {
            var player = (Player) entity;
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
