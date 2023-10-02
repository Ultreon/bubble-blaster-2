package com.ultreon.bubbles.audio;

import com.badlogic.gdx.audio.Sound;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public sealed class SoundInstance permits EmptySoundInstance {
    private static final Set<SoundInstance> ALL = new CopyOnWriteArraySet<>();
    private final long id = -1L;
    private final Sound sound;
    private boolean playing = false;
    private float volume;
    private float pitch;
    private float pan;

    public SoundInstance(SoundEvent soundEvent) {
        this(soundEvent, 1);
    }

    public SoundInstance(SoundEvent soundEvent, float volume) {
        this.volume = volume;
        this.sound = soundEvent.sound;
    }

    public static void stopAll() {
        ALL.forEach(SoundInstance::stop);
    }

    public void play() {
        ALL.add(this);
        this.sound.play(this.volume, this.pitch, this.pan);
        this.playing = true;
    }

    public synchronized void stop() {
        ALL.remove(this);
        this.sound.stop(this.id);
        this.playing = false;
    }

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        this.sound.setVolume(this.id, volume);
        this.volume = volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.sound.setPitch(this.id, pitch);
        this.pitch = pitch;
    }

    public float getPan() {
        return this.pan;
    }

    public void setPan(float pan) {
        this.sound.setPan(this.id, pan, this.volume);
        this.pan = pan;
    }

    public void setPan(float pan, float volume) {
        this.sound.setPan(this.id, pan, volume);
        this.pan = pan;
    }

    public boolean isStopped() {
        return !this.playing;
    }

    public boolean isPlaying() {
        return this.playing;
    }
}
