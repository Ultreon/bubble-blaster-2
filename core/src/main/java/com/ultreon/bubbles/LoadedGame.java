package com.ultreon.bubbles;

import com.ultreon.bubbles.common.Controllable;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.LivingEntity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.DamageType;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.event.v1.EntityEvents;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.MessengerScreen;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.util.Utils;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.util.CollisionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

public class LoadedGame implements Controllable {
    private static final BubbleBlaster GAME = BubbleBlaster.getInstance();

    // Types
    private final Gamemode gamemode;

    private final World world;
    public final ScheduledExecutorService schedulerService = Executors.newScheduledThreadPool(Math.max(Runtime.getRuntime().availableProcessors() / 4, 2));

    // Files / folders.
    private final File saveDir;

    // Active messages.
    private final ArrayList<String> activeMessages = new ArrayList<>();
    private final ArrayList<Long> activeMsgTimes = new ArrayList<>();

    // Flags.
    private boolean running = false;

    // Save
    private final GameSave gameSave;

    private final AutoSaver autoSaver = new AutoSaver(this);

    public LoadedGame(GameSave gameSave, World world) {
        this.gameSave = gameSave;
        this.gamemode = world.getGamemode();
        this.world = world;
        this.saveDir = gameSave.getDirectory();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Show and Hide     //
    ///////////////////////////
    public void begin() {
        Utils.hideCursor();

        this.gamemode.begin();
        this.autoSaver.begin();
        this.running = true;
    }

    public void end() {
        GAME.showScreen(new MessengerScreen("Exiting game world."));

        this.running = false;

        GAME.world = null;

        // Unbind events.
        this.gamemode.end();

        this.world.close();
        this.schedulerService.shutdownNow();

        // Hide cursor.
        Utils.showCursor();

        System.gc();
    }

    private void checkWorldCollision(List<Entity> loopingEntities) {
        for (int a = 0; a < loopingEntities.size(); a++) {
            for (int b = a + 1; b < loopingEntities.size(); b++) {
                try {
                    Entity entityA = loopingEntities.get(a);
                    Entity entityB = loopingEntities.get(b);

                    this.checkCollision(entityA, entityB);
                } catch (RuntimeException e) {
                    BubbleBlaster.getLogger().warn("An exception occurred when checking collision:", e);
                }
            }
        }
    }

    private void checkCollision(Entity entityA, Entity entityB) {
        if (!entityA.isCollidableWith(entityB) && !entityB.isCollidableWith(entityA)) return;
        if (entityA.willBeDeleted() || entityB.willBeDeleted()) return;

        // Check intersection.
        if (CollisionUtil.isColliding(entityA, entityB)) {
            LoadedGame.collideEntities(entityA, entityB);
        }
    }

    private static void collideEntities(Entity entityA, Entity entityB) {
        double delta = 1.0 / TPS;
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
            ((LivingEntity) entityA).damage(entityB.getAttributes().getBase(Attribute.ATTACK) * delta / entityA.getAttributes().getBase(Attribute.DEFENSE), new EntityDamageSource(entityB, DamageType.COLLISION));
        }

        if (entityB instanceof LivingEntity && entityA.doesAttack(entityB) && entityB.canBeAttackedBy(entityA)) {
            ((LivingEntity) entityB).damage(entityA.getAttributes().getBase(Attribute.ATTACK) * delta / entityB.getAttributes().getBase(Attribute.DEFENSE), new EntityDamageSource(entityA, DamageType.COLLISION));
        }
    }

    public void startup() {
    }

    public void shutdown() {

    }

    public boolean isRunning() {
        return this.running;
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
     * Renders the hud, in this method only the system and chat messages.
     * Should not being called, for internal use only.
     *
     * @param game the game instance.
     * @param renderer  a 2D graphics instance.
     */
    public void drawMessages(@SuppressWarnings({"unused", "RedundantSuppression"}) BubbleBlaster game, Renderer renderer) {
        int i = 0;
        for (String s : this.activeMessages) {
            int y = 71 + (32 * i);
            renderer.fill(0, y, 1000, 32, Color.BLACK.withAlpha(0x80));

            renderer.drawText(Fonts.MONOSPACED_14.get(), s, 2, y, Color.WHITE);
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

    public void tick() {
        List<Entity> entities = List.copyOf(this.world.getEntities());

        if (this.world.isInitialized() && !BubbleBlaster.isPaused()) {
            this.checkWorldCollision(entities);
        }

        this.gamemode.tick(this.world);
    }

    public void start() {
        BubbleBlaster.invokeTick(this::begin);
    }

    public Gamemode getGamemode() {
        return this.gamemode;
    }

    public World getWorld() {
        return this.world;
    }

    public GameSave getGameSave() {
        return this.gameSave;
    }
}
