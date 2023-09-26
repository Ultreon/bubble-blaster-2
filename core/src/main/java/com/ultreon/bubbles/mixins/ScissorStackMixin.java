package com.ultreon.bubbles.mixins;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScissorStack.class)
public interface ScissorStackMixin {
    @Accessor
    static Array<Rectangle> getScissors() {
        throw new Error("Mixin failed to apply");
    }
}
