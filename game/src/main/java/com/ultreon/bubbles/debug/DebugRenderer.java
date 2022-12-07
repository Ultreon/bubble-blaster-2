package com.ultreon.bubbles.debug;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.input.KeyInput;
import com.ultreon.bubbles.input.MouseInput;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.Screen;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

public class DebugRenderer {
    private final Font font;
    private final BubbleBlaster game;
    private int lineLeft = 0;
    private int lineRight = 0;

    public DebugRenderer(BubbleBlaster game) {
        this.game = game;
        font = new Font("consolas", Font.PLAIN, 11);
    }

    public void render(Renderer renderer) {
        reset();

        renderer.font(font);
        renderer.color(255, 255, 255);

        Rectangle gameBounds = game.getGameBounds();
        left(renderer, "FPS: " + game.getFps());
        left(renderer, "TPS: " + game.getCurrentTps());
        left(renderer, "RPT: " + game.getGameFrameTime());
        left(renderer, "Game Bounds: (" + gameBounds.x + ", " + gameBounds.y + ") " + gameBounds.width + " x " + gameBounds.height);
        left(renderer, "Scaled Size: " + game.getScaledWidth() + " \u00D7 " + game.getScaledHeight());
        left(renderer, "Window Size: " + game.getGameWindow().getWidth() + " \u00D7 " + game.getGameWindow().getHeight());
        left(renderer, "Canvas Size: " + game.getWidth() + " \u00D7 " + game.getHeight());
        Environment environment = game.environment;
        if (environment != null) {
            GameplayEvent curGe = environment.getCurrentGameEvent();
            left(renderer, "Entity Count: " + environment.getEntities().size());
            left(renderer, "Visible Entity Count: " + environment.getEntities().stream().filter(Entity::isVisible).count());
            left(renderer, "Entity Removal Count: " + environment.getEntities().stream().filter(Entity::willBeDeleted).count());
            left(renderer, "Cur. Game Event: " + (curGe != null ? Registry.GAMEPLAY_EVENTS.getKey(curGe) : "null"));
            left(renderer, "Is Initialized: " + environment.isInitialized());
            left(renderer, "Difficulty: " + environment.getDifficulty().name());
            left(renderer, "Local Difficulty: " + environment.getLocalDifficulty());
            left(renderer, "Seed: " + environment.getSeed());

            if (KeyInput.isDown(KeyInput.Map.KEY_SHIFT)) {
                Entity entityAt = environment.getEntityAt(MouseInput.getPos());
                if (entityAt != null) {
                    left(renderer, "Entity Type: " + Registry.ENTITIES.getKey(entityAt.getType()));
                    if (entityAt instanceof Bubble bubble) {
                        left(renderer, "Bubble Type: " + Registry.BUBBLES.getKey(bubble.getBubbleType()));
                        left(renderer, "Base Speed: " + bubble.getBaseSpeed());
                        left(renderer, "Speed: " + bubble.getSpeed());
                    }
                }
            }

            Player player = environment.getPlayer();
            if (player != null) {
                NumberFormat formatter = new DecimalFormat("#0.00000");
                left(renderer, "Pos: (" + formatter.format(player.getPos().getX()) + ", " + formatter.format(player.getPos().getY()) + ")");
                left(renderer, "Score: " + player.getScore());
                left(renderer, "Level: " + player.getLevel());
                left(renderer, "Speed: " + player.getSpeed());
                left(renderer, "Rotation: " + player.getRotation());
                left(renderer, "Rot Speed: " + player.getRotationSpeed());
            }
        }
        Screen screen = game.getCurrentScreen();
        left(renderer, "Screen: " + (screen == null ? "null" : screen.getClass().getName()));

        for (Map.Entry<String, Long> e : BubbleBlaster.getLastProfile().entrySet()) {
            right(renderer, e.getKey() + " (" + e.getValue() + "ms)");
        }
    }

    private void reset() {
        lineLeft = 0;
        lineRight = 0;
    }

    private void left(Renderer renderer, String text) {
        int line = lineLeft++;
        int height = renderer.fontMetrics(font).getHeight() + 1 + 2;
        int width = renderer.fontMetrics(font).stringWidth(text);
        line++;
        int y = line * height - 3;
        if (game.getCurrentScreen() == null) {
            y += 70;
        }

        renderer.color(0, 0, 0, 0x99);
        renderer.rect(10, y, width + 4, height - 1);
        renderer.color("#fff");
        renderer.text(text, 10 + 2, y + (height - 1 + 1) / 1.5f);
    }

    private void right(Renderer renderer, String text) {
        int line = lineRight++;
        int height = renderer.fontMetrics(font).getHeight() + 1 + 2;
        int width = renderer.fontMetrics(font).stringWidth(text);
        line++;
        int y = line * height - 3;
        if (game.getCurrentScreen() == null) {
            y += 70;
        }

        renderer.color(0, 0, 0, 0x99);
        renderer.rect(game.getWidth() - width - 10, y, width + 4, height - 1);
        renderer.color("#fff");
        renderer.text(text, game.getWidth() - width - 10 + 2, y + (height - 1 + 1) / 1.5f);
    }
}
