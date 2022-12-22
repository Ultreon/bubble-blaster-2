package com.ultreon.bubbles.media;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.exceptions.SoundLoadException;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.commons.function.ThrowingSupplier;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class SoundInstance {
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("GameAudio");
    private final ThrowingSupplier<InputStream, IOException> factory;
    private final String name;
    private boolean playing = false;
    private Player player;

    public SoundInstance(File file) {
        this(file, "");
    }

    public SoundInstance(File file, String name) {
        this(file.toURI(), name);
    }

    public SoundInstance(URI uri) {
        this(uri, "");
    }

    public SoundInstance(URI uri, String name) {
        this(url(uri), name);
    }

    private static URL url(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public SoundInstance(URL url) {
        this(url, "");
    }

    public SoundInstance(URL url, String name) {
        this(url::openStream, name);
    }

    public SoundInstance(Identifier id) {
        this(() -> BubbleBlaster.getInstance().getResourceManager().openResourceStream(id.mapPath(s -> "audio/" + s + ".mp3")));
    }

    public SoundInstance(Identifier id, String name) {
        this(() -> BubbleBlaster.getInstance().getResourceManager().openResourceStream(id.mapPath(s -> "audio/" + s + ".mp3")), name);
    }

    protected SoundInstance(ThrowingSupplier<InputStream, IOException> factory) {
        this(factory, "");
    }

    protected SoundInstance(ThrowingSupplier<InputStream, IOException> factory, String name) {
        this.factory = factory;
        this.name = name;
    }

    private void playClip() throws IOException,
            UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        new Thread(THREAD_GROUP, () -> {
            playing = true;
            try (BufferedInputStream stream = new BufferedInputStream(factory.get())) {
                this.player = new Player(stream);
                try {
                    playThread(player);
                } catch (Exception e) {
                    stop();
                    throw new RuntimeException(e);
                }
                player = null;
            } catch (Exception e) {
                stop();
                throw new SoundLoadException(e);
            }
            playing = false;
        }, "AudioPlayer").start();
    }

    private void playThread(Player player) throws JavaLayerException {
        player.play();
    }

    public Player getPlayer() {
        return player;
    }

    public void play() {
        try {
            playClip();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
            throw new SoundLoadException(e);
        }
        playing = true;
    }

    public synchronized void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
        playing = false;
    }

    public void setVolume(double v) {

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
