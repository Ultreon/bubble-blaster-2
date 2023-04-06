package com.ultreon.bubbles.media;

import com.google.common.base.Suppliers;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registry;

import java.util.function.Supplier;

public class Sound {
    private final Supplier<Identifier> id = Suppliers.memoize(() -> Registry.SOUNDS.getKey(this));
    private BubbleBlaster game;

    public Sound() {

    }

    public Identifier getId() {
        return id.get();
    }

    public MP3Player play() {
        if (game == null) game = BubbleBlaster.getInstance();
        return game.playSound(id.get());
    }

    public SoundInstance play(double volume) {
        if (game == null) game = BubbleBlaster.getInstance();
        return game.playSound(id.get(), volume);
    }
}
