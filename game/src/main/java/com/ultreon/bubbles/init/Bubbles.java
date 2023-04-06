package com.ultreon.bubbles.init;

import com.ultreon.bubbles.bubble.*;
import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.ai.AiAttack;
import com.ultreon.bubbles.entity.ai.AiTarget;
import com.ultreon.bubbles.game.InternalMod;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.object.RegistrySupplier;
import com.ultreon.bubbles.render.Color;
import org.apache.commons.lang3.Range;

import java.util.function.Supplier;

/**
 * Bubble Initialization
 * Bubble init, used for initialize bubbles.
 * For example, the {@link DefenseBoostBubble} instance is assigned here.
 *
 * @see BubbleType
 */
@SuppressWarnings("unused")
public class Bubbles {
    private static final DelayedRegister<BubbleType> REGISTER = DelayedRegister.create(InternalMod.MOD_ID, Registry.BUBBLES);

    // Bubbles
    public static final RegistrySupplier<BubbleType> NORMAL = register("normal", () -> BubbleType.builder()
            .priority(150_000_000L)
            .radius(Range.between(12, 105))
            .speed(Range.between(4.0, 8.7))
            .colors(Color.white)
            .score(1f)
            .build());
    public static final RegistrySupplier<BubbleType> DOUBLE = register("double", () -> BubbleType.builder()
            .priority(4_600_000L)
            .radius(Range.between(24, 75))
            .speed(Range.between(8.0, 17.4))
            .colors(Color.gold, Color.gold)
            .score(2f)
            .build());
    public static final RegistrySupplier<BubbleType> TRIPLE = register("triple", () -> BubbleType.builder()
            .priority(1_150_000L)
            .radius(Range.between(48, 60))
            .speed(Range.between(12.0, 38.8))
            .colors(Color.cyan, Color.cyan, Color.cyan)
            .score(3f)
            .build());
    public static final RegistrySupplier<BubbleType> BOUNCY = register("bouncy", () -> BubbleType.builder()
            .priority(715_000L)
            .radius(Range.between(15, 85))
            .speed(Range.between(3.215d, 4.845d))
            .score(0.625f)
            .colors("#ff0000,#ff3f00,#ff7f00,#ffbf00")
            .bounceAmount(50f)
            .build());
    public static final RegistrySupplier<BubbleType> BUBBLE_FREEZE = register("bubble_freeze", BubbleFreezeBubble::new);
    public static final RegistrySupplier<BubbleType> PARALYZE = register("paralyze", () -> BubbleType.builder()
            .priority(3_325_000L)
            .radius(Range.between(28, 87))
            .speed(Range.between(1.215d, 2.845d))
            .score(0.325f)
            .effect((source, target) -> (new AppliedEffect(Effects.PARALYZE.get(), source.getRadius() / 16, (byte) 1)))
            .colors("#ffff00,#ffff5f,#ffffdf,#ffffff")
            .difficulty(10)
            .build());
    public static final RegistrySupplier<DamageBubble> DAMAGE = register("damage", DamageBubble::new);

    public static final RegistrySupplier<BubbleType> POISON = register("poison", () -> BubbleType.builder()
            .priority(1_313_131L)
            .radius(Range.between(34, 83))
            .speed(Range.between(8.0d, 14.0d))
            .defense(0.225f)
            .attack(0.0f)
            .score(0.375f)
            .hardness(1.0d)
            .colors("#7fff00,#9faf1f,#bf7f3f,#df3f5f,#ff007f")
            .effect((source, target) -> (new AppliedEffect(Effects.POISON.get(), source.getRadius() / 8, 4)))
            .addAiTask(0, new AiAttack())
            .addAiTask(1, new AiTarget(Entities.PLAYER.get()))
            .build());

    public static final RegistrySupplier<HealBubble> HEAL = register("heal", HealBubble::new);
    public static final RegistrySupplier<UltraBubble> ULTRA = register("ultra", UltraBubble::new);
    public static final RegistrySupplier<LevelUpBubble> LEVEL_UP = register("level_up", LevelUpBubble::new);
    public static final RegistrySupplier<HardenedBubble> HARDENED = register("hardened", HardenedBubble::new);
    public static final RegistrySupplier<BlindnessBubble> BLINDNESS = register("blindness", BlindnessBubble::new);
    public static final RegistrySupplier<AccelerateBubble> ACCELERATE = register("accelerate", AccelerateBubble::new);
    public static final RegistrySupplier<SpeedBoostBubble> SPEED_BOOST = register("speed_boost", SpeedBoostBubble::new);
    public static final RegistrySupplier<DoubleStateBubble> DOUBLE_STATE = register("double", DoubleStateBubble::new);
    public static final RegistrySupplier<TripleStateBubble> TRIPLE_STATE = register("triple", TripleStateBubble::new);
    public static final RegistrySupplier<AttackBoostBubble> ATTACK_BOOST = register("attack", AttackBoostBubble::new);
    public static final RegistrySupplier<DefenseBoostBubble> DEFENSE_BOOST = register("defense", DefenseBoostBubble::new);

    private static <T extends BubbleType> RegistrySupplier<T> register(String name, Supplier<T> supplier) {
        return REGISTER.register(name, supplier);
    }

    /**
     * <b>DO NOT CALL, THIS IS CALLED INTERNALLY</b>
     */
    public static void register() {
        REGISTER.register();
    }
}
