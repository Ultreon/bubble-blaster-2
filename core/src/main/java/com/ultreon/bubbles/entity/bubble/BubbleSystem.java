package com.ultreon.bubbles.entity.bubble;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.world.World;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.collections.v0.exceptions.ValueExistsException;
import com.ultreon.libs.collections.v0.list.SizedList;

import java.util.*;

@SuppressWarnings("unused")
public class BubbleSystem {
    protected static HashMap<BubbleType, List<Long>> bubblePriorities = new HashMap<>();
    protected static long maxPriority = 0L;
    private static final SizedList<BubbleType> defaults = new SizedList<>();
    private static final SizedList<BubbleType> priorities = new SizedList<>();
    private static boolean active;

    public static void begin() {
        active = true;
    }

    public static void end() {
        active = false;
    }

    public static double getDefaultPriority(BubbleType bubble) {
        int index = defaults.indexOf(bubble);
        if (index == -1) {
            return 0d;
        }

        return Objects.requireNonNullElse(defaults.getSize(index), 0.0);
    }

    public static double getDefaultTotalPriority() {
        return defaults.getTotalSize();
    }

    public static double getDefaultPercentageChance(BubbleType bubble) {
        return BubbleSystem.getDefaultPriority(bubble) / BubbleSystem.getDefaultTotalPriority();
    }

    public static double getPriority(BubbleType bubble) {
        int index = priorities.indexOf(bubble);
        if (index == -1) {
            return 0d;
        }


        return Objects.requireNonNullElse(priorities.getSize(index), 0.0);
    }

    public static double getTotalPriority() {
        return priorities.getTotalSize();
    }

    public static double getPercentageChance(BubbleType bubble) {
        return BubbleSystem.getPriority(bubble) / BubbleSystem.getTotalPriority();
    }

    /**
     * {@linkplain BubbleType Bubble} initialization for random spawning.
     *
     * @see BubbleType
     * @see Registries#BUBBLES
     */
    public static void init() {
        Collection<BubbleType> bubbleTypes = Registries.BUBBLES.values();
        BubbleSystem.bubblePriorities = new HashMap<>();
        BubbleSystem.maxPriority = 0;

        if (bubbleTypes == null)
            throw new NullPointerException();

        defaults.clear();
        priorities.clear();

        for (BubbleType bubbleType : bubbleTypes) {
            try {
                priorities.add(bubbleType.getPriority(), bubbleType);
                defaults.add(bubbleType.getPriority(), bubbleType);
            } catch (ValueExistsException valueExists) {
                BubbleBlaster.LOGGER.warn("Error occurred in bubble system initialization:");
            }
        }
    }

    /**
     * Returns a random bubble from the bubbles initialized in {@link #init()}.
     *
     * @param random The random instance used for the bubble system e.g. {@code bubbles:bubble_system} from the initDefaults in {@link Gamemode}.
     * @return A random bubble.
     */
    public static BubbleType random(RandomSource random, World world) {
        double localDifficulty = world.getLocalDifficulty();
        priorities.editLengths((bubbleType2) -> bubbleType2.getModifiedPriority(localDifficulty));

        double index = random.nextDouble(0, priorities.getTotalSize());
        return priorities.getValue(index);
    }
}
