package dev.ultreon.bubbles.audio;

import com.badlogic.gdx.audio.Music;
import com.google.common.base.Suppliers;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.registry.RegisterHandler;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class MusicEvent implements RegisterHandler {
    private final Supplier<Identifier> id = Suppliers.memoize(() -> Registries.MUSIC.getKey(this));
    private Music music;
    private boolean stopped;

    public MusicEvent() {

    }

    @Override
    public void onRegister(@NotNull Identifier id) {
        // Since this method is called in the loading thread:
        // we need to invoke the initialization into the rendering thread, and wait.
        this.music = BubbleBlaster.invokeAndWait(() -> BubbleBlaster.newMusic(id.mapPath(s -> "music/" + s + ".mp3")));
    }

    public Identifier getId() {
        return this.id.get();
    }

    public void play() {
        this.music.play();
        this.stopped = false;
        this.music.setOnCompletionListener(this::onComplete);
    }

    private void onComplete(Music music) {
        if (music != this.music) return;

        this.stopped = true;
    }

    public void pause() {
        this.music.pause();
    }

    public void stop() {
        this.music.stop();
        this.stopped = true;
    }

    public float getVolume() {
        return this.music.getVolume();
    }

    public void setVolume(float volume) {
        this.music.setVolume(volume);
    }

    public float getPosition() {
        return this.music.getPosition();
    }

    public void setPosition(float position) {
        this.music.setPosition(position);
    }

    public boolean isLooping() {
        return this.music.isLooping();
    }

    public void setLooping(boolean looping) {
        this.music.setLooping(looping);
    }

    public boolean isPlaying() {
        return this.music.isPlaying();
    }

    public boolean isPaused() {
        return !this.music.isPlaying() && !this.stopped;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public void dispose() {
        this.music.dispose();
    }
}
