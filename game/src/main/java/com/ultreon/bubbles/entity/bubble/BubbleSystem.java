package com.ultreon.bubbles.entity.bubble;

import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.exceptions.ValueExists;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.collections.v0.exceptions.ValueExistsException;
import com.ultreon.libs.collections.v0.list.SizedList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class BubbleSystem {
    protected static HashMap<BubbleType, List<Long>> bubblePriorities = new HashMap<>();
    protected static long maxPriority = 0L;
    private static final SizedList<BubbleType> defaults = new SizedList<>();
    private static final SizedList<BubbleType> priorities = new SizedList<>();

    public static SizedList<BubbleType> getPriorities() {
        return priorities;
    }

    public static SizedList<BubbleType> getDefaultsPriorities() {
        return defaults;
    }

    public static Double getDefaultPriority(BubbleType bubble) {
        int index = defaults.indexOf(bubble);
        if (index == -1) {
            return 0d;
        }

        return defaults.getSize(index);
    }

    public static Double getDefaultTotalPriority() {
        return defaults.getTotalSize();
    }

    public static Double getDefaultPercentageChance(BubbleType bubble) {
        return getDefaultPriority(bubble) / (double) getDefaultTotalPriority();
    }

    public static Double getPriority(BubbleType bubble) {
        int index = priorities.indexOf(bubble);
        if (index == -1) {
            return 0d;
        }

        return priorities.getSize(index);
    }

    public static Double getTotalPriority() {
        return priorities.getTotalSize();
    }

    public static Double getPercentageChance(BubbleType bubble) {
        return (double) getPriority(bubble) / getTotalPriority();
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

        if (bubbleTypes == null) {
            throw new NullPointerException();
        }

        for (BubbleType bubbleType : bubbleTypes) {
            try {
                priorities.add(bubbleType.getPriority(), bubbleType);
                defaults.add(bubbleType.getPriority(), bubbleType);
            } catch (ValueExistsException valueExists) {
                valueExists.printStackTrace();
            }
        }
    }

    /**
     * Returns a random bubble from the bubbles initialized in {@link #init()}.
     *
     * @param rand The random instance used for the bubble system e.g. {@code bubbles:bubble_system} from the initDefaults in {@link Gamemode}.
     * @return A random bubble.
     */
    public static BubbleType random(Rng rand, long spawnIndex, int retry, Environment env) {
        double localDifficulty = env.getLocalDifficulty();
        priorities.editLengths((bubbleType2) -> bubbleType2.getModifiedPriority(localDifficulty));

        double randValue = rand.getNumber(0, priorities.getTotalSize(), spawnIndex, retry);

        return priorities.getValue(randValue);
    }
}
