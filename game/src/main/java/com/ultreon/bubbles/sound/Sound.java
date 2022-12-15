package com.ultreon.bubbles.sound;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.resources.Resource;

public class Sound {
    protected BubbleBlaster game = BubbleBlaster.getInstance();
    protected String id;

    public Sound() {

    }

    public Identifier getLocation() {
        return Registry.SOUNDS.getKey(this);
    }

    public void load() {
        Identifier location = getLocation();
        String id = getLocation().toString();

        Resource resource = game.getResourceManager().getResource(location.mapPath(s -> "audio/" + s + ".ogg"));
        if (resource == null) {
            BubbleBlaster.getLogger().warn("Can't load sound resource: " + location);
            return;
        }

        this.id = id;
        game.getSoundSystem().loadSound(resource.getUrl(), id);
    }

    public SoundInstance play() {
        if (id == null) {
            return SoundInstance.EMPTY;
        }
        return game.playSound(this);
    }

    public SoundInstance play(float volume) {
        return game.playSound(this, volume);
    }

    public String getId() {
        return id;
    }
}
