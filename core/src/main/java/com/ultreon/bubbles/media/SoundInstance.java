package com.ultreon.bubbles.media;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.resources.ResourceFileHandle;
import com.ultreon.libs.commons.v0.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SoundInstance {
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("GameAudio");
    private static final Map<Identifier, Sound> SOUNDS_BY_ID = new HashMap<>();
    private final Sound sound;
    private final String name;
    private boolean playing = false;
    private static final Set<SoundInstance> ALL = new CopyOnWriteArraySet<>();
    private long id;

    public SoundInstance(Identifier id) {
        this(cache(id), id.toString());
    }

    @Deprecated(forRemoval = true)
    public SoundInstance(Identifier id, String name) {
        this(cache(id), name);
    }

    protected SoundInstance(Sound sound) {
        this(sound, "");
    }

    protected SoundInstance(Sound sound, String name) {
        this.sound = sound;
        this.name = name;
    }

    private static Sound cache(Identifier id) {
        var identifier = id.mapPath(s -> "audio/" + s + ".mp3");

        final Sound sound = SOUNDS_BY_ID.get(identifier);
        if (sound != null) return sound;

        Sound newSound = Gdx.audio.newSound(new ResourceFileHandle(identifier));
        SOUNDS_BY_ID.put(identifier, newSound);
        return newSound;
    }

    public static void stopAll() {
        ALL.forEach(SoundInstance::stop);
    }

    private void playClip() {

    }

    public void play() {
        Thread audioPlayer = new Thread(THREAD_GROUP, () -> {
            try {
                playing = true;
                ALL.add(this);
                this.id = sound.play();
                ALL.remove(this);
                playing = false;
            } catch (Exception e) {
                BubbleBlaster.getLogger().error("Sound #" + this.id + " (" + name + ") failed to play:", e);
            }
        }, "AudioPlayer");
        audioPlayer.setDaemon(true);
        audioPlayer.start();
        playing = true;
    }

    public synchronized void stop() {
        playing = false;
        sound.stop(id);
    }

    public void setVolume(float v) {
        sound.setVolume(this.id, v);
    }

    public double getVolume() {
        return 1.0;
    }

    public boolean isStopped() {
        return !playing;
    }

    public boolean isPlaying() {
        return playing;
    }

    public String getName() {
        return name;
    }
}
