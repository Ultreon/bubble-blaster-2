package com.ultreon.bubbles.media;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.game.BubbleBlaster;

public class Sound {
    private final Identifier location;
    private BubbleBlaster game;

    public Sound(Identifier location) {
        this.location = location;
    }

    public Identifier getLocation() {
        return location;
    }

    public MP3Player play() {
        if (game == null) game = BubbleBlaster.getInstance();
        return game.playSound(location);
    }

    public SoundInstance play(double volume) {
        if (game == null) game = BubbleBlaster.getInstance();
        return game.playSound(location, volume);
    }
}
