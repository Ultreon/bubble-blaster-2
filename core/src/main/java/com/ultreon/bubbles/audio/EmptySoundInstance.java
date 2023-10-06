package com.ultreon.bubbles.audio;

public final class EmptySoundInstance extends SoundInstance {
    EmptySoundInstance(SoundEvent soundEvent) {
        super(soundEvent);
    }

    @Override
    public void play() {

    }

    @Override
    public synchronized void stop() {

    }

    @Override
    public void setVolume(float v) {

    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public float getPitch() {
        return 1;
    }

    @Override
    public void setPitch(float pitch) {

    }

    @Override
    public float getPan() {
        return 0;
    }

    @Override
    public void setPan(float pan) {

    }

    @Override
    public void setPan(float pan, float volume) {

    }
}
