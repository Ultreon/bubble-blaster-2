package com.ultreon.bubbles.init;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.sound.Sound;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.object.RegistrySupplier;

public class Sounds {
    private static final DelayedRegister<Sound> REGISTER = DelayedRegister.create(BubbleBlaster.NAMESPACE, Registry.SOUNDS);

    //******************//
    //     Gameplay     //
    //******************//
    public static final RegistrySupplier<Sound> BUBBLE_POP = REGISTER.register("sfx/bubble/pop", Sound::new);

    //************************//
    //     User Interface     //
    //************************//
    public static final RegistrySupplier<Sound> UI_BUTTON_FOCUS_CHANGE = REGISTER.register("sfx/ui/button/focus_change", Sound::new);

    //***************//
    //     Music     //
    //***************//
    public static final RegistrySupplier<Sound> MUSIC_SUBMARINE = REGISTER.register("bgm/submarine", Sound::new);
    public static final RegistrySupplier<Sound> MUSIC_ULTIMA = REGISTER.register("bgm/ultima", Sound::new);

    public static void register() {
        REGISTER.register();
    }
}
