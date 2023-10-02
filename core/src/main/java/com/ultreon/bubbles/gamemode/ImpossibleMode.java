package com.ultreon.bubbles.gamemode;

import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.init.BubbleTypes;
import com.ultreon.bubbles.world.World;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
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
