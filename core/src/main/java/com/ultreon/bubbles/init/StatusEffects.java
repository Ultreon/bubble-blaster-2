package com.ultreon.bubbles.init;

import com.ultreon.bubbles.effect.*;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.DelayedRegister;

/**
 * Effect Initialization
 * Effect init, used for initialize effects (temporary effects for players / bubbles that do something overtime).
 * For example, the {@link ResistanceStatusEffect} instance is assigned here.
 *
 * @see StatusEffect
 * @see DelayedRegister<StatusEffect>
 */
@SuppressWarnings("unused")
public class StatusEffects {
    public static final StatusEffect NONE = register("none", new StatusEffect() {
        @Override
        protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
            return false;
        }
    });

    public static final ResistanceStatusEffect DEFENSE_BOOST = register("defense", new ResistanceStatusEffect());

    public static final BubbleFreezeStatusEffect BUBBLE_FREEZE = register("bubble_freeze", new BubbleFreezeStatusEffect());

    public static final AttackBoostEffect ATTACK_BOOST = register("attack", new AttackBoostEffect());
    public static final ScoreStatusEffect MULTI_SCORE = register("multi_score", new ScoreStatusEffect());
    public static final SpeedStatusEffect SPEED_BOOST = register("speed_boost", new SpeedStatusEffect());
    public static final BlindnessStatusEffect BLINDNESS = register("blindness", new BlindnessStatusEffect());
    public static final ParalyzeStatusEffect PARALYZE = register("paralyze", new ParalyzeStatusEffect());
    public static final PoisonStatusEffect POISON = register("poison", new PoisonStatusEffect());
    public static final LuckStatusEffect LUCK = register("luck", new LuckStatusEffect());

    private static <T extends StatusEffect> T register(String name, T effect) {
        Registries.EFFECTS.register(new Identifier(name), effect);
        return effect;
    }

    public static void register() {

    }
}
