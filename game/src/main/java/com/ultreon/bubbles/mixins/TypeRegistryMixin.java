package com.ultreon.bubbles.mixins;

import com.ultreon.data.TypeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TypeRegistry.class)
public class TypeRegistryMixin {
    @Inject(method = "getId", at = @At("HEAD"))
    private static void bubblesDbg$injectGetId(Class<?> componentType, CallbackInfoReturnable<Integer> cir) {
        System.out.println("componentType = " + componentType);
    }
}
