package com.ultreon.bubbles.media;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SoundPlayer {
    private int maximal;
    private final ArrayList<SoundInstance> slots;

    public SoundPlayer(int maximal) {
        this.maximal = maximal;
        slots = new ArrayList<>();
    }

    public void add(SoundInstance slot) {
        if (slots.size() >= maximal) return;

        slots.add(slot);

        if (!slot.isPlaying()) slot.play();
    }

    public void addIf(SoundInstance slot, Predicate<SoundInstance> predicate) {
        if (slots.size() >= maximal) return;

        boolean flag = false;
        for (SoundInstance slot1 : slots) {
            boolean flag1 = predicate.test(slot1);
            if (flag1) {
                flag = true;
            }
        }

        if (!flag) return;

        slots.add(slot);

        if (!slot.isPlaying()) slot.play();
    }

    public boolean removeIf(Predicate<SoundInstance> filter) {
        return slots.removeIf(filter);
    }

    public boolean removeStopped() {
        return removeIf(SoundInstance::isStopped);
    }

    public boolean removePlaying() {
        return removeIf(SoundInstance::isPlaying);
    }

    public boolean remove(SoundInstance slot) {
        return slots.remove(slot);
    }

    public SoundInstance remove(int index) {
        return slots.remove(index);
    }

    public SoundInstance get(int index) {
        return slots.get(index);
    }

    public void stopAll() {
        for (SoundInstance slot : slots) {
            slot.stop();
        }
    }

    public void playAll() {
        for (SoundInstance slot : slots) {
            slot.play();
        }
    }

    public boolean isSilent() {
        for (SoundInstance slot : slots) {
            if (slot.isPlaying()) return false;
        }
        return true;
    }

    public void clear() {
        for (SoundInstance slot : slots) {
            slot.stop();
        }
        slots.clear();
    }

    public int getMaximal() {
        return maximal;
    }

    public void setMaximal(int maximal) {
        this.maximal = maximal;
    }

    public List<SoundInstance> getSlots() {
        return slots;
    }
}
