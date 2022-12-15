package com.ultreon.bubbles.sound;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.game.BubbleBlaster;

public final class LogoSound extends Sound {
    private static LogoSound instance;

    public LogoSound() {

    }

    public static LogoSound get() {
        return instance;
    }

    public static void create() {
        instance = new LogoSound();
        instance.load();
    }

    @Override
    public Identifier getLocation() {
        return BubbleBlaster.id("sfx/logo_reveal");
    }

    @Override
    public void load() {
        String id = getLocation().toString();
//        game.getSoundSystem().loadSound(BubbleBlaster.getGameJar().child("/assets/bubbles/audio/sfx/logo_reveal.ogg"), id + ".ogg");
        this.id = id;
    }
}
