package com.ultreon.bubbles.media;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SoundPlayer {
    private int maximal;
    private final ArrayList<Sound> slots;

    public SoundPlayer(int maximal) {
        this.maximal = maximal;
        slots = new ArrayList<>();
    }

    public void add(Sound slot) {
        if (slots.size() >= maximal) return;

        slots.add(slot);

        if (!slot.isPlaying()) slot.play();
    }

    public void addIf(Sound slot, Predicate<Sound> predicate) {
        if (slots.size() >= maximal) return;

        boolean flag = false;
        for (Sound slot1 : slots) {
            boolean flag1 = predicate.test(slot1);
            if (flag1) {
                flag = true;
            }
        }

        if (!flag) return;

        slots.add(slot);

        if (!slot.isPlaying()) slot.play();
    }

    public boolean removeIf(Predicate<Sound> filter) {
        return slots.removeIf(filter);
    }

    public boolean removeStopped() {
        return removeIf(Sound::isStopped);
    }

    public boolean removePlaying() {
        return removeIf(Sound::isPlaying);
    }

    public boolean remove(Sound slot) {
        return slots.remove(slot);
    }

    public Sound remove(int index) {
        return slots.remove(index);
    }

    public Sound get(int index) {
        return slots.get(index);
    }

    public void stopAll() {
        for (Sound slot : slots) {
            slot.stop();
        }
    }

    public void playAll() {
        for (Sound slot : slots) {
            slot.play();
        }
    }

    public boolean isSilent() {
        for (Sound slot : slots) {
            if (slot.isPlaying()) return false;
        }
        return true;
    }

    public void clear() {
        for (Sound slot : slots) {
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

    public List<Sound> getSlots() {
        return slots;
    }
}
