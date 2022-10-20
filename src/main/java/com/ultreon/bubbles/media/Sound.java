package com.ultreon.bubbles.media;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.game.BubbleBlaster;
import javafx.scene.media.AudioClip;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class Sound {
    private final AudioClip clip;
    private final String name;
    private boolean stopped = false;

    public Sound(File file) {
        this(file, "");
    }

    public Sound(File file, String name) {
        clip = new AudioClip(file.toURI().toString());
        this.name = name;
    }

    public Sound(URI uri) {
        this(uri, "");
    }

    public Sound(URI uri, String name) {
        clip = new AudioClip(uri.toString());
        this.name = name;
    }

    public Sound(URL url) throws URISyntaxException {
        this(url, "");
    }

    public Sound(Identifier id) throws URISyntaxException {
        this(Objects.requireNonNull(BubbleBlaster.class.getResource("/assets/" + id.location() + "/audio/" + id.path()), "Audio resource not found: " + id), "");
    }

    public Sound(Identifier id, String name) throws URISyntaxException {
        this(Objects.requireNonNull(BubbleBlaster.class.getResource("/assets/" + id.location() + "/audio/" + id.path() + ".mp3"), "Audio resource not found: " + id), name);
    }

    public Sound(URL url, String name) throws URISyntaxException {
        clip = new AudioClip(url.toURI().toString());
        this.name = name;
    }

    public AudioClip getClip() {
        return clip;
    }

    public void play() {
        clip.play();
        stopped = false;
    }

    public void stop() {
        clip.stop();
        stopped = true;
    }

    public double getBalance() {
        return clip.getBalance();
    }

    public double getRate() {
        return clip.getRate();
    }

    public void setVolume(double v) {
        clip.setVolume(v);
    }

    public void setMute(double v) {
        clip.setBalance(v);
    }

    public double getVolume() {
        return clip.getVolume();
    }

    public String getSource() {
        return clip.getSource();
    }

    public boolean isStopped() {
        return stopped;
    }

    public boolean isPlaying() {
        return !stopped;
    }

    public String getName() {
        return name;
    }
}
