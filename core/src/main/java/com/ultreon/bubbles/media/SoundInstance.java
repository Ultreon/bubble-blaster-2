package com.ultreon.bubbles.media;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.bubbles.common.exceptions.SoundLoadException;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.commons.function.ThrowingSupplier;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SoundInstance {
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("GameAudio");
    private final ThrowingSupplier<InputStream, IOException> factory;
    private final String name;
    private boolean playing = false;
    private Player player;
    private static final Set<SoundInstance> ALL = new CopyOnWriteArraySet<>();

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

    public static void stopAll() {
        ALL.forEach(SoundInstance::stop);
    }

    private void playClip() throws IOException,
            UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        Thread audioPlayer = new Thread(THREAD_GROUP, () -> {
            playing = true;
            try (BufferedInputStream stream = new BufferedInputStream(factory.get())) {
                this.player = new Player(stream);
                ALL.add(this);
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
            ALL.remove(this);
            playing = false;
        }, "AudioPlayer");
        audioPlayer.setDaemon(false);
        audioPlayer.start();
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
        playing = false;
        if (player != null) {
            player.close();
            player = null;
        }
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
