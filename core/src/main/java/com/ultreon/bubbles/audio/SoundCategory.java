package com.ultreon.bubbles.audio;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@ApiStatus.Experimental
public class SoundCategory {
    public static final SoundCategory DEFAULT = new SoundCategory(128);
    public static final SoundCategory MUSIC = new SoundCategory(1);
    private int maximal;
    private final ArrayList<SoundInstance> slots;

    public SoundCategory(int maximal) {
        this.maximal = maximal;
        this.slots = new ArrayList<>();
    }

    public void add(SoundInstance slot) {
        if (this.slots.size() >= this.maximal) return;

        this.slots.add(slot);

        if (!slot.isPlaying()) slot.play();
    }

    public void addIf(SoundInstance slot, Predicate<SoundInstance> predicate) {
        if (this.slots.size() >= this.maximal) return;

        boolean flag = false;
        for (SoundInstance slot1 : this.slots) {
            boolean flag1 = predicate.test(slot1);
            if (flag1) {
                flag = true;
            }
        }

        if (!flag) return;

        this.slots.add(slot);

        if (!slot.isPlaying()) slot.play();
    }

    public boolean removeIf(Predicate<SoundInstance> filter) {
        return this.slots.removeIf(filter);
    }

    public boolean removeStopped() {
        return this.removeIf(SoundInstance::isStopped);
    }

    public boolean removePlaying() {
        return this.removeIf(SoundInstance::isPlaying);
    }

    public boolean remove(SoundInstance slot) {
        return this.slots.remove(slot);
    }

    public SoundInstance remove(int index) {
        return this.slots.remove(index);
    }

    public SoundInstance get(int index) {
        return this.slots.get(index);
    }

    public void stopAll() {
        for (SoundInstance slot : this.slots) {
            slot.stop();
        }
    }

    public void playAll() {
        for (SoundInstance slot : this.slots) {
            slot.play();
        }
    }

    public boolean isSilent() {
        for (SoundInstance slot : this.slots) {
            if (slot.isPlaying()) return false;
        }
        return true;
    }

    public void clear() {
        for (SoundInstance slot : this.slots) {
            slot.stop();
        }
        this.slots.clear();
    }

    public int getMaximal() {
        return this.maximal;
    }

    public void setMaximal(int maximal) {
        this.maximal = maximal;
    }

    public List<SoundInstance> getSlots() {
        return this.slots;
    }
}
