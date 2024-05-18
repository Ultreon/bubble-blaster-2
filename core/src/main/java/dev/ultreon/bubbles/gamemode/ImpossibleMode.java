package dev.ultreon.bubbles.gamemode;

import dev.ultreon.bubbles.bubble.BubbleType;
import dev.ultreon.bubbles.init.BubbleTypes;
import dev.ultreon.bubbles.random.RandomSource;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.bubbles.util.annotation.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author XyperCode
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ImpossibleMode extends NormalMode {
    public ImpossibleMode() {
        super();
    }

    @Override
    public @Nullable BubbleType randomBubble(RandomSource random, World world) {
        return BubbleTypes.DAMAGE;
    }
}
