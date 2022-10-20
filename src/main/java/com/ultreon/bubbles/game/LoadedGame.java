package com.ultreon.bubbles.game;

import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.common.random.PseudoRandom;
import com.ultreon.bubbles.entity.LivingEntity;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.DamageSourceType;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v2.EntityEvents;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.media.Sound;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.MessengerScreen;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.util.Util;
import com.ultreon.commons.util.CollisionUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LoadedGame {

    // Fonts.
    private final Font defaultFont = new Font(Util.getGame().getPixelFontName(), Font.PLAIN, 32);

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
    private volatile Sound ambientAudio;
    private long nextAudio;

    // Flags.
    @SuppressWarnings("FieldCanBeLocal")
    private boolean gameActive = false;

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

        activate();

        this.environment.getGamemode().start();

        autoSaver.start();

        this.collisionThread = new Thread(this::collisionThread, "Collision");
        this.collisionThread.start();

        this.ambientAudioThread = new Thread(this::ambientAudioThread, "audio-Thread");
        this.ambientAudioThread.start();
    }

    public void quit() {
        game.showScreen(new MessengerScreen("Exiting game environment."));

        game.environment = null;

        // Unbind events.
        gamemode.destroy();
        deactivate();

        ambientAudio.stop();

        if (autoSaveThread != null) autoSaveThread.interrupt();
        if (collisionThread != null) collisionThread.interrupt();
        if (ambientAudioThread != null) ambientAudioThread.interrupt();
        if (gameEventHandlerThread != null) gameEventHandlerThread.interrupt();

        environment.quit();

        // Hide cursor.
        Util.setCursor(BubbleBlaster.getInstance().getDefaultCursor());

        System.gc();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Thread methods     //
    ////////////////////////////
    private void ambientAudioThread() {
        while (this.gameActive) {
            if (this.ambientAudio == null) {
                if (!this.environment.isBloodMoonActive() && this.nextAudio < System.currentTimeMillis()) {
                    if (new PseudoRandom(System.nanoTime()).getNumber(0, 5, -1) == 0) {
                        try {
                            this.ambientAudio = new Sound(Objects.requireNonNull(getClass().getResource("/assets/bubbles/audio/bgm/submarine.mp3")), "ambient");
                            this.ambientAudio.setVolume(0.5d);
                            this.ambientAudio.play();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                            BubbleBlaster.getInstance().shutdown();
                        }
                    } else {
                        this.nextAudio = System.currentTimeMillis() + new Random().nextLong(1000, 2000);
                    }
                } else if (this.environment.isBloodMoonActive()) {
                    try {
                        this.ambientAudio = new Sound(Objects.requireNonNull(getClass().getResource("/assets/bubbles/audio/bgm/ultima.mp3")), "blood_moon_state");
                        this.ambientAudio.setVolume(0.4d);
                        this.ambientAudio.play();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        BubbleBlaster.getInstance().shutdown();
                    }
                }
            } else if (this.ambientAudio.isStopped() && this.environment.isBloodMoonActive() && this.ambientAudio.getName().equals("blood_moon_state")) {
                this.ambientAudio = null;
                this.environment.stopBloodMoon();
            } else if (this.ambientAudio.isStopped()) {
                this.ambientAudio = null;
            } else if (!this.ambientAudio.getClip().isPlaying()) {
                this.ambientAudio.stop();
                this.ambientAudio = null;
            } else if (this.environment.isBloodMoonActive() && !this.ambientAudio.getName().equals("blood_moon_state")) {
                this.ambientAudio.stop();
                this.ambientAudio = null;

                try {
                    this.ambientAudio = new Sound(Objects.requireNonNull(getClass().getResource("/assets/bubbles/audio/bgm/ultima.mp3")), "blood_moon_state");
                    this.ambientAudio.setVolume(0.25d);
                    this.ambientAudio.play();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    BubbleBlaster.getInstance().shutdown();
                }
            }
        }
    }

    private void collisionThread() {
        // Initiate variables for game loop.
        long lastTime = System.nanoTime();
        double amountOfUpdates = 30.0;
        double ns = 1000000000 / amountOfUpdates;
        double delta;

        while (this.gameActive) {
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
    public void activate() {
        BubbleBlaster.getEventBus().subscribe(this);
        this.gameActive = true;
    }

    public void deactivate() {
        this.gameActive = false;
    }

    public boolean isGameActive() {
        return this.gameActive;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Getter-Only Properties     //
    ////////////////////////////////////
    public Sound getAmbientAudio() {
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

            subInstance.color(new Color(0, 0, 0, 128));
            subInstance.rect(0, 0, 1000, 32);

            subInstance.color(new Color(255, 255, 255, 255));
            GraphicsUtils.drawLeftAnchoredString(subInstance, s, new Point2D.Double(2, 2), 28, defaultFont);

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
