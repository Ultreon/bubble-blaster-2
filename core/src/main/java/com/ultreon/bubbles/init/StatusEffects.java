package com.ultreon.bubbles.init;

import com.ultreon.bubbles.effect.*;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.DelayedRegister;

/**
 * Effect Initialization
 * Effect init, used for initialize effects (temporary effects for players / bubbles that do something overtime).
 * For example, the {@link DefenseBoostEffect} instance is assigned here.
 *
 * @see StatusEffect
 * @see DelayedRegister<StatusEffect>
 */
@SuppressWarnings("unused")
public class StatusEffects {
    public static final StatusEffect NONE = register("none", new StatusEffect() {
        @Override
        protected boolean canExecute(Entity entity, AppliedEffect appliedEffect) {
            return false;
        }
    });

    public static final DefenseBoostEffect DEFENSE_BOOST = register("defense", new DefenseBoostEffect());

    public static final BubbleFreezeEffect BUBBLE_FREEZE = register("bubble_freeze", new BubbleFreezeEffect());

    public static final AttackBoostEffect ATTACK_BOOST = register("attack", new AttackBoostEffect());
    public static final MultiScoreEffect MULTI_SCORE = register("multi_score", new MultiScoreEffect());
    public static final SpeedBoostEffect SPEED_BOOST = register("speed_boost", new SpeedBoostEffect());
    public static final BlindnessEffect BLINDNESS = register("blindness", new BlindnessEffect());
    public static final ParalyzeEffect PARALYZE = register("paralyze", new ParalyzeEffect());
    public static final PoisonEffect POISON = register("poison", new PoisonEffect());
    public static final LuckEffect LUCK = register("luck", new LuckEffect());

    private static <T extends StatusEffect> T register(String name, T effect) {
        Registries.EFFECTS.register(new Identifier(name), effect);
        return effect;
    }

    public static void register() {

    }
}
