package com.ultreon.bubbles.init;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.media.Sound;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;

public class Sounds {
    private static final DelayedRegister<Sound> REGISTER = DelayedRegister.create(BubbleBlaster.NAMESPACE, Registry.SOUNDS);

    public static void register() {
        REGISTER.register();
    }
}
