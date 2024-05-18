package dev.ultreon.bubbles.audio;

import com.badlogic.gdx.audio.Sound;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SoundInstance {
    private static final Set<SoundInstance> ALL = new CopyOnWriteArraySet<>();
    private final long id = -1L;
    private final Sound sound;
    private float volume;
    private float pitch = 1;
    private float pan;

    public SoundInstance(SoundEvent soundEvent) {
        this(soundEvent, 1);
    }

    public SoundInstance(SoundEvent soundEvent, float volume) {
        this(soundEvent, volume, soundEvent.getRandomSource().nextFloat(0.95f, 1.05f));
    }

    public SoundInstance(SoundEvent soundEvent, float volume, float pitch) {
        this(soundEvent, volume, pitch, 0f);
    }

    public SoundInstance(SoundEvent soundEvent, float volume, float pitch, float pan) {
        this.volume = volume;
        this.pan = pan;
        this.pitch = pitch;
        this.sound = soundEvent.sound;
    }

    public static void stopAll() {
        ALL.forEach(SoundInstance::stop);
    }

    public void play() {
        ALL.add(this);
        this.sound.play(this.volume, this.pitch, this.pan);
    }

    public synchronized void stop() {
        ALL.remove(this);
        this.sound.stop(this.id);
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
}
