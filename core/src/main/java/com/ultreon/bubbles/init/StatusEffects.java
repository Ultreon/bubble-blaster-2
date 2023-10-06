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
public class StatusEffects {
    public static final StatusEffect NONE = StatusEffects.register("none", new StatusEffect() {
        @Override
        protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
            return false;
        }
    });

    public static final ResistanceStatusEffect RESISTANCE = StatusEffects.register("resistance", new ResistanceStatusEffect());

    public static final AttackBoostStatusEffect ATTACK_BOOST = StatusEffects.register("attack", new AttackBoostStatusEffect());
    public static final ScoreStatusEffect SCORE = StatusEffects.register("score", new ScoreStatusEffect());
    public static final SwiftnessStatusEffect SWIFTNESS = StatusEffects.register("swiftness", new SwiftnessStatusEffect());
    public static final BlindnessStatusEffect BLINDNESS = StatusEffects.register("blindness", new BlindnessStatusEffect());
    public static final BubbleFreezeEffect BUBBLE_FREEZE = StatusEffects.register("bubble_freeze", new BubbleFreezeEffect());
    public static final ParalyzeStatusEffect PARALYZE = StatusEffects.register("paralyze", new ParalyzeStatusEffect());
    public static final PoisonStatusEffect POISON = StatusEffects.register("poison", new PoisonStatusEffect());
    public static final LuckStatusEffect LUCK = StatusEffects.register("luck", new LuckStatusEffect());

    private static <T extends StatusEffect> T register(String name, T effect) {
        Registries.EFFECTS.register(new Identifier(name), effect);
        return effect;
    }

    public static void register() {

    }
}
