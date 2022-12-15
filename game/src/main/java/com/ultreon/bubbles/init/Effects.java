package com.ultreon.bubbles.init;

import com.ultreon.bubbles.game.InternalMod;
import com.ultreon.bubbles.effect.*;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.object.RegistrySupplier;

import java.util.function.Supplier;

/**
 * Effect Initialization
 * Effect init, used for initialize effects (temporary effects for players / bubbles that do something overtime).
 * For example, the {@link DefenseBoostEffect} instance is assigned here.
 *
 * @see StatusEffect
 * @see DelayedRegister<StatusEffect>
 */
@SuppressWarnings("unused")
public class Effects {
    private static final DelayedRegister<StatusEffect> REGISTER = DelayedRegister.create(InternalMod.MOD_ID, Registry.EFFECTS);

    public static final RegistrySupplier<DefenseBoostEffect> DEFENSE_BOOST = register("defense", DefenseBoostEffect::new);
    public static final RegistrySupplier<BubbleFreezeEffect> BUBBLE_FREEZE = register("bubble_freeze", BubbleFreezeEffect::new);
    public static final RegistrySupplier<AttackBoostEffect> ATTACK_BOOST = register("attack", AttackBoostEffect::new);
    public static final RegistrySupplier<MultiScoreEffect> MULTI_SCORE = register("multi_score", MultiScoreEffect::new);
    public static final RegistrySupplier<SpeedBoostEffect> SPEED_BOOST = register("speed_boost", SpeedBoostEffect::new);
    public static final RegistrySupplier<BlindnessEffect> BLINDNESS = register("blindness", BlindnessEffect::new);
    public static final RegistrySupplier<ParalyzeEffect> PARALYZE = register("paralyze", ParalyzeEffect::new);
    public static final RegistrySupplier<PoisonEffect> POISON = register("poison", PoisonEffect::new);
    public static final RegistrySupplier<LuckEffect> LUCK = register("Luck", LuckEffect::new);

    private static <T extends StatusEffect> RegistrySupplier<T> register(String name, Supplier<T> effectSupplier) {
        return REGISTER.register(name, effectSupplier);
    }

    /**
     * <b>DO NOT CALL, THIS IS CALLED INTERNALLY</b>
     */
    public static void register() {
        REGISTER.register();
    }
}
