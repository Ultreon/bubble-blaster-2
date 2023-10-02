package com.ultreon.bubbles.mixins;

import generated_bcfb74d8fef4c8a73600.gl.GLScissorState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true) // Enables exporting for the targets of this mixin
@Mixin(targets = "com.badlogic.gdx.backends.lwjgl3.Lwjgl3GL20")
public class GL20Mixin {
    @Inject(at = @At("HEAD"), method = "glScissor")
    public void inject$glScissor(int x, int y, int width, int height, CallbackInfo ci) {
        GLScissorState.glScissor(x, y, width, height);
//        ci.cancel();
    }
}
