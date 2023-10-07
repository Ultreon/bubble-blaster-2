package com.ultreon.bubbles.audio;

import com.badlogic.gdx.utils.Disposable;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.random.valuesource.ValueSource;
import com.ultreon.bubbles.util.Randomizer;
import com.ultreon.libs.datetime.v0.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class MusicSystem implements Iterator<@Nullable MusicEvent>, Disposable {
    private double untilNext;
    @NotNull
    private final ValueSource delay;
    private final ValueSource timeBetween;
    @Nullable
    private MusicEvent music;
    @NotNull
    private MusicEvent nextMusic;
    private final List<MusicEvent> musicList;
    private volatile boolean isDisposed;
    private boolean paused;
    private boolean enabled;

    public MusicSystem(ValueSource delay, ValueSource timeBetween, List<MusicEvent> musicList) {
        this.untilNext = delay.getValue();
        this.delay = delay;
        this.timeBetween = timeBetween;
        this.musicList = musicList;
        this.nextMusic = Randomizer.choose(this.musicList);
    }

    public void play() {
        var music = this.music;
        if (music != null) {
            music.play();
        }
        this.paused = false;
        this.enabled = true;
    }

    public void pause() {
        this.paused = true;
        var music = this.music;
        if (music != null) {
            music.pause();
        }
    }

    public void stop() {
        var music = this.music;
        if (music != null) {
            music.stop();
        }
        this.enabled = false;
        this.untilNext = this.delay.getValue();
    }
    @Override
    public boolean hasNext() {
        return !this.isDisposed;
    }

    @Override
    @Nullable
    @CanIgnoreReturnValue
    public MusicEvent next() {
        if (this.music != null) {
            this.music.stop();
        }
        var old = this.music;
        this.music = this.nextMusic;
        this.nextMusic = this.choose(old);
        this.music.play();
        System.out.println("this.music = " + this.music);
        return this.music;
    }

    @NotNull
    private MusicEvent choose(MusicEvent old) {
        var reties = 3;
        var chosen = this.nextMusic;
        while (reties > 0) {
            chosen = Randomizer.choose(this.musicList);
            if (chosen != old || this.musicList.size() == 1) return chosen;

            reties--;
        }

        return chosen;
    }

    @Override
    public void remove() {
        Iterator.super.remove();
    }

    public void update(float deltaTime) {
        if (!this.enabled) return;
        if (this.paused) return;

        var music = this.music;
        if (music != null && music.isStopped()) {
            this.untilNext = this.timeBetween.getValue();
        }

        this.untilNext = this.untilNext - deltaTime;

        if (this.untilNext <= 0 && !this.isStopped())
            this.next();
    }

    public boolean isStopped() {
        if (this.music != null) return this.music.isStopped();
        else return true;
    }

    public Duration getUntilNext() {
        return Duration.ofMilliseconds((long) (this.untilNext * 1000.0));
    }

    @Override
    public void dispose() {
        this.isDisposed = true;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public @Nullable MusicEvent getCurrentlyPlaying() {
        return this.music;
    }
}
