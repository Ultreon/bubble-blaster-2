package com.ultreon.bubbles.audio;

import com.badlogic.gdx.audio.Sound;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.random.JavaRandom;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.registry.RegisterHandler;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class SoundEvent implements RegisterHandler {
    public static final SoundEvent EMPTY = new SoundEvent() {
        @Override
        public SoundInstance play() {
            return new EmptySoundInstance(this);
        }
    };
    private final RandomSource randomSource = new JavaRandom();
    private final boolean randomPitch;

    Sound sound;

    public SoundEvent() {
        this(false);
    }

    public SoundEvent(boolean randomPitch) {
        this.randomPitch = randomPitch;
    }

    @Override
    public void onRegister(@NotNull Identifier id) {
        // Since this method is called in the loading thread:
        // we need to invoke the initialization into the rendering thread, and wait.
        this.sound = BubbleBlaster.invokeAndWait(() -> BubbleBlaster.newSound(id.mapPath(s -> "sounds/" + s + ".mp3")));
    }

    @UnknownNullability
    public Identifier getId() {
        return Registries.SOUNDS.getKey(this);
    }

    @CanIgnoreReturnValue
    public SoundInstance play() {
        SoundInstance instance = new SoundInstance(this);
        instance.play();
        return instance;
    }

    @CanIgnoreReturnValue
    public SoundInstance play(float volume) {
        SoundInstance instance = this.randomPitch ? new SoundInstance(this, volume) : new SoundInstance(this, volume, 1f);
        instance.play();
        return instance;
    }

    public void dispose() {
        this.sound.dispose();
    }

    public RandomSource getRandomSource() {
        return this.randomSource;
    }

    public boolean isRandomPitch() {
        return this.randomPitch;
    }
}
