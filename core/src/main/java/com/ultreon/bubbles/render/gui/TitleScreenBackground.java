package com.ultreon.bubbles.render.gui;

import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.init.BubbleTypes;
import com.ultreon.bubbles.random.JavaRandom;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.ValueSource;
import com.ultreon.bubbles.world.WorldRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TitleScreenBackground {
    private static final BubbleType TYPE = BubbleTypes.NORMAL;
    private static final int MAX_BUBBLES = 500;
    private final RandomSource random = new JavaRandom();
    private final List<FakeBubble> bubbles = new CopyOnWriteArrayList<>();
    private final int width;
    private final int height;

    public TitleScreenBackground(int width, int height) {
        this.width = width;
        this.height = height;

        for (int i = 0; i <= MAX_BUBBLES; i++) {
            this.addBubbleInit();
        }
    }

    private void addBubble() {
        FakeBubble fakeBubble = new FakeBubble(TYPE.getRadius(), TYPE.getSpeed());
        fakeBubble.position.x = this.width + fakeBubble.radius;
        fakeBubble.position.y = this.random.nextFloat(-fakeBubble.radius, this.height + fakeBubble.radius);
        this.bubbles.add(fakeBubble);
    }

    private void addBubbleInit() {
        FakeBubble fakeBubble = new FakeBubble(TYPE.getRadius(), TYPE.getSpeed());
        fakeBubble.position.x = this.random.nextFloat(-fakeBubble.radius, this.width + fakeBubble.radius);
        fakeBubble.position.y = this.random.nextFloat(-fakeBubble.radius, this.height + fakeBubble.radius);
        this.bubbles.add(fakeBubble);
    }

    public void tick() {
        List<FakeBubble> toRemove = new ArrayList<>();
        for (FakeBubble bubble : this.bubbles) {
            bubble.position.add(-bubble.speed, 0);
            if (bubble.position.x < -bubble.radius) {
                toRemove.add(bubble);
            }
        }

        this.bubbles.removeAll(toRemove);

        if (this.bubbles.size() < MAX_BUBBLES) {
            this.addBubble();
        }
    }

    public void render(Renderer renderer) {
        renderer.enableBlur(15);
        renderer.fillGradient(0, 0, this.width, this.height, WorldRenderer.BG_TOP, WorldRenderer.BG_BOTTOM);

        for (FakeBubble fakeBubble : this.bubbles)
            WorldRenderer.drawBubble(renderer, fakeBubble.position.x, fakeBubble.position.y, fakeBubble.radius, 0, TYPE);

        renderer.disableBlur();
        renderer.fillGradient(0, 0, this.width, this.height, Color.BLACK.withAlpha(0x80), Color.BLACK.withAlpha(0x90));
    }

    public void dispose() {
        this.bubbles.clear();
    }

    private static class FakeBubble {
        private final float radius;
        private final float speed;
        private final Vector2 position = new Vector2();

        public FakeBubble(ValueSource radius, ValueSource speed) {
            this.radius = (float) radius.getValue();
            this.speed = (float) speed.getValue();
        }
    }
}
