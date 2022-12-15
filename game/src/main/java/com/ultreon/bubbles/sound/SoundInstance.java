package com.ultreon.bubbles.sound;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.game.BubbleBlaster;
import paulscode.sound.SoundSystem;

public class SoundInstance {
    public static final SoundInstance EMPTY = new SoundInstance(null, null) {
        public final SoundInstance wrap = this;
        public final Sound sound = new Sound() {
            {
                id = "missingno";
            }

            @Override
            public void load() {

            }

            @Override
            public SoundInstance play() {
                return wrap;
            }

            @Override
            public SoundInstance play(float volume) {
                return wrap;
            }

            @Override
            public String getId() {
                return id;
            }
        };

        @Override
        public void play() {

        }

        @Override
        public synchronized void pause() {

        }

        @Override
        public synchronized void stop() {

        }

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public boolean isPaused() {
            return false;
        }

        @Override
        public boolean isStopped() {
            return true;
        }

        @Override
        public double getVolume() {
            return 0.0;
        }

        @Override
        public void setVolume(float v) {

        }

        @Override
        public synchronized float getPitch() {
            return 1;
        }

        @Override
        public synchronized void setPitch(float pitch) {

        }

        @Override
        public synchronized void setPosition(float x, float y) {

        }

        @Override
        public Identifier getId() {
            return BubbleBlaster.emptyId();
        }

        @Override
        public Sound getSound() {
            return sound;
        }
    };
    private final SoundSystem soundSystem;
    private final Sound sound;
    private final String id;
    private boolean paused;

    public SoundInstance(Sound sound, String id) {
        this.sound = sound;
        this.id = id;
        this.soundSystem = BubbleBlaster.getInstance().getSoundSystem();
    }

    public void play() {
        soundSystem.play(id);
        paused = false;
    }

    public boolean isPlaying() {
        return soundSystem.playing(id);
    }

    public synchronized void stop() {
        soundSystem.stop(id);
    }

    public boolean isStopped() {
        return !soundSystem.playing(id) && !paused;
    }

    public synchronized void pause() {
        soundSystem.pause(id);
        paused = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public synchronized void setPosition(float x, float y) {
        soundSystem.setPosition(id, x, y, 0);
    }

    public synchronized void setPitch(float pitch) {
        soundSystem.setPitch(id, pitch);
    }

    public synchronized float getPitch() {
        return soundSystem.getPitch(id);
    }

    public void setVolume(float v) {
        soundSystem.setVolume(id, v);
    }

    public double getVolume() {
        return soundSystem.getVolume(id);
    }

    public Identifier getId() {
        return sound.getLocation();
    }

    public Sound getSound() {
        return sound;
    }
}
