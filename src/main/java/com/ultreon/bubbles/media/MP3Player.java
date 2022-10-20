package com.ultreon.bubbles.media;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MP3Player {
    private final InputStream inputStream;
    private final String name;
    private Player jlPlayer;
    private static final Map<String, MP3Player> playing = new HashMap<>();

    public MP3Player(String name, InputStream inputStream) {
        this.inputStream = inputStream;
        this.name = name;
    }

    public void play() {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            jlPlayer = new Player(bufferedInputStream);
        } catch (Exception e) {
            System.out.println("Problem playing mp3 file " + name);
            System.out.println(e.getMessage());
        }

        if (playing.containsKey(name)) {
            throw new IllegalArgumentException("Sound already playing: " + name);
        }

        new Thread(() -> {
            playing.put(name, this);
            try {
                jlPlayer.play();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            playing.remove(name);
        }).start();


    }
    
    public void close() {
        if (jlPlayer != null) jlPlayer.close();
        playing.remove(name);
    }

    public boolean isPlaying() {
        return playing.containsKey(name);
    }
}