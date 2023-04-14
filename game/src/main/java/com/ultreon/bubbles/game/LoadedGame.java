package com.ultreon.bubbles.game;

import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.common.random.PseudoRandom;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.LivingEntity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.DamageSourceType;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v2.EntityEvents;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.media.SoundInstance;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.MessengerScreen;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.util.Util;
import com.ultreon.commons.util.CollisionUtil;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LoadedGame {
    private static final BubbleBlaster game = BubbleBlaster.getInstance();

    // Types
    private final Gamemode gamemode;

    private final Environment environment;

    // Threads.
    private Thread autoSaveThread;
    private Thread collisionThread;
    private Thread ambientAudioThread;
    private Thread gameEventHandlerThread;

    // Files / folders.
    private final File saveDir;

    // Active messages.
    private final ArrayList<String> activeMessages = new ArrayList<>();
    private final ArrayList<Long> activeMsgTimes = new ArrayList<>();

    // Audio.
    private volatile SoundInstance ambientAudio;
    private long nextAudio;

    // Flags.
    @SuppressWarnings("FieldCanBeLocal")
    private boolean running = false;

    // Save
    private final GameSave gameSave;
    private GameplayEvent currentGameplayEvent;

    private final AutoSaver autoSaver = new AutoSaver(this);

    public LoadedGame(GameSave gameSave, Environment environment) {
        this.gameSave = gameSave;
        this.gamemode = environment.getGamemode();
        this.environment = environment;
        this.saveDir = gameSave.getDirectory();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Show and Hide     //
    ///////////////////////////
    private void run() {
        Util.setCursor(BubbleBlaster.getInstance().getBlankCursor());

        startup();

        this.environment.getGamemode().start();

        autoSaver.start();

        this.collisionThread = new Thread(this::collisionThread, "Collision");
        this.collisionThread.setDaemon(false);
        this.collisionThread.start();

        this.ambientAudioThread = new Thread(this::ambientAudioThread, "audio-Thread");
        this.ambientAudioThread.setDaemon(false);
        this.ambientAudioThread.start();
    }

    public void quit() {
        game.showScreen(new MessengerScreen("Exiting game environment."));

        game.environment = null;

        // Unbind events.
        gamemode.destroy();
        shutdown();

        if (ambientAudio != null) {
            ambientAudio.stop();
        }

        if (autoSaveThread != null) autoSaveThread.interrupt();
        if (collisionThread != null) collisionThread.interrupt();
        if (ambientAudioThread != null) ambientAudioThread.interrupt();
        if (gameEventHandlerThread != null) gameEventHandlerThread.interrupt();

        environment.shutdown();

        // Hide cursor.
        Util.setCursor(BubbleBlaster.getInstance().getDefaultCursor());

        System.gc();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Thread methods     //
    ////////////////////////////
    private void ambientAudioThread() {
        while (this.running) {
            if (this.ambientAudio == null) {
                if (!this.environment.isBloodMoonActive() && this.nextAudio < System.currentTimeMillis()) {
                    if (new PseudoRandom(System.nanoTime()).getNumber(0, 5, -1) == 0) {
                        this.ambientAudio = new SoundInstance(Objects.requireNonNull(getClass().getResource("/assets/bubbles/audio/bgm/submarine.mp3")), "ambient");
                        this.ambientAudio.setVolume(0.1d);
                        this.ambientAudio.play();
                    } else {
                        this.nextAudio = System.currentTimeMillis() + new Random().nextLong(1000, 2000);
                    }
                } else if (this.environment.isBloodMoonActive()) {
                    this.ambientAudio = new SoundInstance(Objects.requireNonNull(getClass().getResource("/assets/bubbles/audio/bgm/ultima.mp3")), "blood_moon_state");
                    this.ambientAudio.setVolume(0.25d);
                    this.ambientAudio.play();
                }
            } else if (this.ambientAudio.isStopped() && this.environment.isBloodMoonActive() && this.ambientAudio.getName().equals("blood_moon_state")) {
                this.ambientAudio = null;
                this.environment.stopBloodMoon();
            } else if (this.ambientAudio.isStopped()) {
                this.ambientAudio = null;
            } else if (!this.ambientAudio.isPlaying()) {
                this.ambientAudio.stop();
                this.ambientAudio = null;
            } else if (this.environment.isBloodMoonActive() && !this.ambientAudio.getName().equals("blood_moon_state")) {
                this.ambientAudio.stop();
                this.ambientAudio = null;

                this.ambientAudio = new SoundInstance(Objects.requireNonNull(getClass().getResource("/assets/bubbles/audio/bgm/ultima.mp3")), "blood_moon_state");
                this.ambientAudio.setVolume(0.25d);
                this.ambientAudio.play();
            }
        }
    }

    private void collisionThread() {
        // Initiate variables for game loop.
        long lastTime = System.nanoTime();
        double amountOfUpdates = 30.0;
        double ns = 1000000000 / amountOfUpdates;
        double delta;

        while (this.running && game.isRunning()) {
            // Calculate tick delta-time.
            long now = System.nanoTime();
            delta = (now - lastTime) / ns;
            lastTime = now;

            java.util.List<Entity> entities = this.environment.getEntities();

            List<Entity> loopingEntities = new ArrayList<>(entities);

            if (environment.isInitialized()) {
                if (!BubbleBlaster.isPaused()) {
                    for (int a = 0; a < loopingEntities.size(); a++)
                        for (int b = a + 1; b < loopingEntities.size(); b++) {
                            Entity entityA = loopingEntities.get(a);
                            Entity entityB = loopingEntities.get(b);

                            if (!entityA.isCollidableWith(entityB) && !entityB.isCollidableWith(entityA)) {
                                continue;
                            }

                            try {
                                // Check intersection.
                                if (CollisionUtil.isColliding(entityA, entityB)) {
                                    if (entityA.isCollidableWith(entityB)) {
                                        // Handling collision by posting collision event, and let the intersected entities attack each other.
                                        EntityEvents.COLLISION.factory().onCollision(delta, entityA, entityB);
                                        entityA.onCollision(entityB, delta);
                                    }

                                    if (entityB.isCollidableWith(entityA)) {
                                        EntityEvents.COLLISION.factory().onCollision(delta, entityB, entityA);
                                        entityB.onCollision(entityA, delta);
                                    }

                                    if (entityA instanceof LivingEntity && entityB.doesAttack(entityA) && entityA.canBeAttackedBy(entityB)) {
                                        ((LivingEntity) entityA).damage(entityB.getAttributes().getBase(Attribute.ATTACK) * delta / entityA.getAttributes().getBase(Attribute.DEFENSE), new EntityDamageSource(entityB, DamageSourceType.COLLISION));
                                    }

                                    if (entityB instanceof LivingEntity && entityA.doesAttack(entityB) && entityB.canBeAttackedBy(entityA)) {
                                        ((LivingEntity) entityB).damage(entityA.getAttributes().getBase(Attribute.ATTACK) * delta / entityB.getAttributes().getBase(Attribute.DEFENSE), new EntityDamageSource(entityA, DamageSourceType.COLLISION));
                                    }
                                }
                            } catch (ArrayIndexOutOfBoundsException exception) {
                                BubbleBlaster.getLogger().info("Array index was out create bounds! Check check double check!");
                            }
                        }
                }
            }
        }

        BubbleBlaster.getLogger().info("Collision thread will die now.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Event Binding     //
    ///////////////////////////
    public void startup() {
        this.running = true;
    }

    public void shutdown() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Getter-Only Properties     //
    ////////////////////////////////////
    public SoundInstance getAmbientAudio() {
        return this.ambientAudio;
    }

    public File getSaveDir() {
        return this.saveDir;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Trigger Methods     //
    /////////////////////////////
    public void receiveMessage(String s) {
        this.activeMessages.add(s);
        this.activeMsgTimes.add(System.currentTimeMillis() + 3000);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Render Methods     //
    ////////////////////////////

    /**
     * Renders the game, such as the HUD, or environment.
     * Should not being called, for internal use only.
     *
     * @param game the game instance.
     * @param renderer  a 2D graphics instance.
     */
    @Deprecated(forRemoval = true, since = "0.0.3047")
    public void render(BubbleBlaster game, Renderer renderer) {
        if (this.environment.isInitialized()) {
            this.gamemode.render(renderer);
        }
        this.renderHUD(game, renderer);
    }

    /**
     * Renders the hud, in this method only the system and chat messages.
     * Should not being called, for internal use only.
     *
     * @param game the game instance.
     * @param renderer  a 2D graphics instance.
     */
    public void renderHUD(@SuppressWarnings({"unused", "RedundantSuppression"}) BubbleBlaster game, Renderer renderer) {
        int i = 0;
        for (String s : activeMessages) {
            Renderer subInstance = renderer.subInstance(0, 71 + (32 * i), 1000, 32);

            subInstance.color(Color.argb(0x80000000));
            subInstance.rect(0, 0, 1000, 32);

            subInstance.color(Color.argb(0xffffffff));
            Fonts.MONOSPACED.get().draw(subInstance, s, 32, 2, 16);

            subInstance.dispose();
            i++;
        }

        for (i = 0; i < this.activeMessages.size(); i++) {
            if (this.activeMsgTimes.get(i) < System.currentTimeMillis()) {
                this.activeMsgTimes.remove(i);
                this.activeMessages.remove(i);
                i--;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Utility methods     //
    /////////////////////////////

    @Deprecated
    public void tick() {
        this.gamemode.tick(environment);
    }

    public void start() {
        BubbleBlaster.runOnMainThread(this::run);
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public GameSave getGameSave() {
        return gameSave;
    }
}
