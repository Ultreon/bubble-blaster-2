package com.ultreon.bubbles.init;

import com.ultreon.bubbles.bubble.*;
import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.ai.AiAttack;
import com.ultreon.bubbles.entity.ai.AiTarget;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.libs.commons.v0.Identifier;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.ApiStatus;

/**
 * Bubble Initialization
 * Bubble init, used for initialize bubbles.
 * For example, the {@link DefenseBoostBubble} instance is assigned here.
 *
 * @see BubbleType
 */
@SuppressWarnings("unused")
public class Bubbles {
    // Bubbles
    public static final BubbleType NORMAL = register("normal", BubbleType.builder()
            .priority(150_000_000L)
            .radius(Range.between(12, 105))
            .speed(Range.between(4.0, 8.7))
            .colors(Color.white)
            .score(1f)
            .build());
    public static final BubbleType DOUBLE = register("double", BubbleType.builder()
            .priority(4_600_000L)
            .radius(Range.between(24, 75))
            .speed(Range.between(8.0, 17.4))
            .colors(Color.gold, Color.gold)
            .score(2f)
            .build());
    public static final BubbleType TRIPLE = register("triple", BubbleType.builder()
            .priority(1_150_000L)
            .radius(Range.between(48, 60))
            .speed(Range.between(12.0, 38.8))
            .colors(Color.cyan, Color.cyan, Color.cyan)
            .score(3f)
            .build());
    public static final BubbleType BOUNCY = register("bouncy", BubbleType.builder()
            .priority(715_000L)
            .invulnerable()
            .radius(Range.between(15, 85))
            .speed(Range.between(3.215d, 4.845d))
            .score(0.625f)
            .colors("#ff0000,#ff3f00,#ff7f00,#ffbf00")
            .bounceAmount(50f)
            .build());
    public static final BubbleType BUBBLE_FREEZE = register("bubble_freeze", new BubbleFreezeBubble());
    public static final BubbleType PARALYZE = register("paralyze", BubbleType.builder()
            .priority(3_325_000L)
            .radius(Range.between(28, 87))
            .speed(Range.between(1.215d, 2.845d))
            .score(0.325f)
            .effect((source, target) -> (new AppliedEffect(StatusEffects.PARALYZE, source.getRadius() / 16, (byte) 1)))
            .colors("#ffff00,#ffff5f,#ffffdf,#ffffff")
            .difficulty(10)
            .build());
    public static final DamageBubble DAMAGE = register("damage", new DamageBubble());

    public static final BubbleType POISON = register("poison", BubbleType.builder()
            .priority(1_313_131L)
            .radius(Range.between(34, 83))
            .speed(Range.between(8.0d, 14.0d))
            .defense(0.225f)
            .attack(0.0f)
            .score(0.375f)
            .hardness(1.0d)
            .colors("#7fff00,#9faf1f,#bf7f3f,#df3f5f,#ff007f")
            .effect((source, target) -> (new AppliedEffect(StatusEffects.POISON, source.getRadius() / 8, 4)))
            .addAiTask(0, new AiAttack())
            .addAiTask(1, new AiTarget(Entities.PLAYER))
            .build());

    public static final HealBubble HEAL = register("heal", new HealBubble());
    public static final UltraBubble ULTRA = register("ultra", new UltraBubble());
    public static final LevelUpBubble LEVEL_UP = register("level_up", new LevelUpBubble());
    public static final HardenedBubble HARDENED = register("hardened", new HardenedBubble());
    public static final BlindnessBubble BLINDNESS = register("blindness", new BlindnessBubble());
    public static final AccelerateBubble ACCELERATE = register("accelerate", new AccelerateBubble());
    public static final SpeedBoostBubble SPEED_BOOST = register("speed_boost", new SpeedBoostBubble());
    public static final DoubleStateBubble DOUBLE_STATE = register("double", new DoubleStateBubble());
    public static final TripleStateBubble TRIPLE_STATE = register("triple", new TripleStateBubble());
    public static final AttackBoostBubble ATTACK_BOOST = register("attack", new AttackBoostBubble());
    public static final DefenseBoostBubble DEFENSE_BOOST = register("defense", new DefenseBoostBubble());

    private static <T extends BubbleType> T register(String name, T bubbleType) {
        Registries.BUBBLES.register(new Identifier(name), bubbleType);
        return bubbleType;
    }

    @ApiStatus.Internal
    public static void register() {

    }
}
