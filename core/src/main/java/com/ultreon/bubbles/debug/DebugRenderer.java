package com.ultreon.bubbles.debug;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.event.v1.InputEvents;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.input.KeyboardInput;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.commons.v0.size.FloatSize;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.registries.v0.RegistrySupplier;
import com.ultreon.libs.text.v1.MutableText;
import com.ultreon.libs.text.v1.TextObject;

import java.util.*;

public class DebugRenderer {
    private static final Formatter<Object> DEFAULT_FORMATTER = new Formatter<>(Object.class, BubbleBlaster.id("object")) {
        @Override
        public void format(Object obj, IFormatterContext context) {
            Class<?> c = obj.getClass();

            context.classValue(c);
            context.identifier("@");
            context.hexValue(obj.hashCode());
        }
    };
    private final RegistrySupplier<BitmapFont> font;
    private final BubbleBlaster game;
    private int yLeft;
    private int yRight;
    private Thread selectedThread = null;
    private Map<Thread, ThreadSection> lastProfile;
    private String selectInput = "";
    private final Stack<String> pathStack = new Stack<>();
    private final Stack<Section> sectionStack = new Stack<>();
    private final GlyphLayout layout = new GlyphLayout();

    public DebugRenderer(BubbleBlaster game) {
        this.game = game;
        this.font = Fonts.MONOSPACED_14;

        InputEvents.KEY_RELEASE.listen(this::keyPress);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void format(Object obj, IFormatterContext context) {
        if (obj == null) {
            context.keyword("null");
        } else if (obj instanceof Class<?> c) {
            context.packageName(c.getPackage().getName() + ".");
            context.className(c.getSimpleName());
        } else {
            Formatter identified = FormatterRegistry.identify(obj.getClass());
            if (identified != null) {
                identified.format(obj, context);
                return;
            }

            DEFAULT_FORMATTER.format(obj, context);
        }

    }

    public void render(Renderer renderer) {
        if (this.game.isLoading()) return;

        this.reset();

        renderer.setColor(255, 255, 255);

        Rectangle gameBounds = this.game.getGameBounds();
        this.left(renderer, "FPS", this.game.getFps());
        this.left(renderer, "TPS", this.game.getCurrentTps());
        this.left(renderer, "RPT", this.game.getGameFrameTime());
        this.left(renderer, "Game Bounds", gameBounds);
        this.left(renderer, "Scaled Size", new FloatSize(this.game.getScaledWidth(), this.game.getScaledHeight()));
        this.left(renderer, "Window Size", new FloatSize(this.game.getGameWindow().getWidth(), this.game.getGameWindow().getHeight()));
        this.left(renderer, "Canvas Size", new FloatSize(this.game.getWidth(), this.game.getHeight()));
        World world = this.game.world;
        if (world != null) {
            GameplayEvent curGe = world.getCurrentGameEvent();
            this.left(renderer, "Entity Count", world.getEntities().size());
            this.left(renderer, "Visible Entity Count", world.getEntities().stream().filter(Entity::isVisible).count());
            this.left(renderer, "Entity Removal Count", world.getEntities().stream().filter(Entity::willBeDeleted).count());
            this.left(renderer, "Cur. Game Event", (curGe != null ? Registries.GAMEPLAY_EVENTS.getKey(curGe) : null));
            this.left(renderer, "Is Initialized", world.isInitialized());
            this.left(renderer, "Difficulty", world.getDifficulty().name());
            this.left(renderer, "Local Difficulty", world.getLocalDifficulty());
            this.left(renderer, "Seed", world.getSeed());

            if (KeyboardInput.isKeyDown(Input.Keys.SHIFT_LEFT)) {
                GridPoint2 pos = KeyboardInput.getMousePoint();
                Entity entityAt = world.getEntityAt(new Vec2i(pos.x, pos.y));
                if (entityAt != null) {
                    this.left(renderer, "Entity Type", Registries.ENTITIES.getKey(entityAt.getType()));
                    if (entityAt instanceof Bubble bubble) {
                        this.left(renderer, "Bubble Type", Registries.BUBBLES.getKey(bubble.getBubbleType()));
                        this.left(renderer, "Base Speed:", bubble.getAttributes().getBase(Attribute.SPEED));
                        this.left(renderer, "Speed", bubble.getAttributes().get(Attribute.SPEED));
                        this.left(renderer, "Radius", bubble.getRadius());
                    }
                }
            }

            Player player = world.getPlayer();
            if (player != null) {
                this.left(renderer, "Pos", player.getPos());
                this.left(renderer, "Score", player.getScore());
                this.left(renderer, "Level", player.getLevel());
                this.left(renderer, "Speed", player.getSpeed());
                this.left(renderer, "Rotation", player.getRotation());
                this.left(renderer, "Rot Speed", player.getRotationSpeed());
                this.left(renderer, "Acceleration", player.accel);
                this.left(renderer, "Velocity", player.velocity);
                this.left(renderer, "Temp Velocity", player.tempVel);
                this.left(renderer, "BoostAccelTimer", player.boostAccelTimer);
                this.left(renderer, "BoostRefillTimer", player.boostRefillTimer);
                this.left(renderer, "DstToMouseCursor", player.distanceTo(KeyboardInput.getMousePos()));
            }
        }
        Screen screen = this.game.getCurrentScreen();
        this.left(renderer, "Screen", (screen == null ? null : screen.getClass()));

        this.lastProfile = BubbleBlaster.getLastProfile();
        if (this.selectedThread == null) {
            List<Thread> threads = Lists.newArrayList(this.lastProfile.keySet());
            threads.sort(Comparator.comparing(Thread::getName));

            MutableText selInputText = TextObject.literal(this.selectInput);
            selInputText.setColor(Color.rgb(0xff4040).toAwt());
            MutableText typingText = TextObject.literal("Typing ");
            typingText.setColor(Color.rgb(0xffa040).toAwt());
            this.right(renderer, typingText, selInputText);
//            right(renderer, "-----------------");

            for (int i = 0, threadsSize = threads.size(); i < threadsSize; i++) {
                Thread e = threads.get(i);
                MutableText literal = TextObject.literal(" [" + e.getName() + "]");
                literal.setColor(Color.rgb(0xff4040).toAwt());
                MutableText thread = TextObject.literal("Thread");
                MutableText index = TextObject.literal("[" + (i + 1) + "] ");
                index.setColor(Color.rgb(0x30ff30).toAwt());
                this.right(renderer, index.append(thread), literal);
            }
        } else {
            ArrayList<Map.Entry<String, Section>> entries;
            long millis;

            if (this.sectionStack.isEmpty()) {
                entries = Lists.newArrayList(this.lastProfile.get(this.selectedThread).getValues().entrySet());
                entries.sort(Map.Entry.comparingByKey());
                millis = entries.stream().mapToLong(e -> e.getValue().getMillis()).sum();
            } else {
                Section peek = this.sectionStack.peek();
                millis = peek.getMillis();

                entries = Lists.newArrayList(peek.getValues().entrySet());
                entries.sort(Map.Entry.comparingByKey());
            }

            MutableText curMillisText = TextObject.literal(" (" + millis + "ms)");
            curMillisText.setColor(Color.rgb(0xff4040).toAwt());
            MutableText curSectionText = TextObject.literal("Current Section: ");
            curSectionText.setColor(Color.rgb(0xffa040).toAwt());
            this.right(renderer, curSectionText, curMillisText);
            MutableText selInputText = TextObject.literal(this.selectInput);
            selInputText.setColor(Color.rgb(0xff4040).toAwt());
            MutableText typingText = TextObject.literal("Typing ");
            typingText.setColor(Color.rgb(0xffa040).toAwt());
            this.right(renderer, typingText, selInputText);
//            right(renderer, "-----------------");
            for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
                Map.Entry<String, Section> e = entries.get(i);
                MutableText millisText = TextObject.literal(" (" + e.getValue().getMillis() + "ms)");
                millisText.setColor(Color.rgb(0xff4040).toAwt());
                MutableText name = TextObject.literal(e.getKey());
                MutableText index = TextObject.literal("[" + (i + 1) + "] ");
                index.setColor(Color.rgb(0x30ff30).toAwt());
                this.right(renderer, index.append(name), millisText);
            }
        }
    }

    private void keyPress(int keyCode) {
        if (keyCode == Input.Keys.ENTER) {
            System.out.println("selectInput = " + this.selectInput);
            if (this.selectInput.isEmpty()) return;
            int number;
            try {
                number = Integer.parseInt(this.selectInput);
            } catch (NumberFormatException e) {
                return;
            }
            this.selectInput = "";
            System.out.println("number = " + number);
            number--;
            if (this.selectedThread == null) {
                List<Thread> threads = Lists.newArrayList(this.lastProfile.keySet());

                if (number >= threads.size()) return;

                threads.sort(Comparator.comparing(Thread::getName));
                this.selectedThread = threads.get(number);
                System.out.println("selectedThread = " + this.selectedThread);
            } else {
                ArrayList<Map.Entry<String, Section>> entries;

                if (this.sectionStack.isEmpty()) {
                    entries = Lists.newArrayList(this.lastProfile.get(this.selectedThread).getValues().entrySet());
                    entries.sort(Map.Entry.comparingByKey());
                } else {
                    Section peek = this.sectionStack.peek();

                    entries = Lists.newArrayList(peek.getValues().entrySet());
                    entries.sort(Map.Entry.comparingByKey());
                }

                if (number >= entries.size()) return;

                var entry = entries.get(number);
                this.sectionStack.push(entry.getValue());
                this.pathStack.push(entry.getKey());
                System.out.println("entry = " + entry.getKey());
            }
        } else if (keyCode == Input.Keys.FORWARD_DEL) {
            if (this.selectedThread == null) return;
            if (this.pathStack.isEmpty()) return;
            this.sectionStack.pop();
            this.pathStack.pop();
        } else if (keyCode == Input.Keys.BACKSPACE) {
            if (this.selectInput.isEmpty()) return;
            this.selectInput = this.selectInput.substring(0, this.selectInput.length() - 1);
        } else if (keyCode >= Input.Keys.NUM_0 && keyCode <= Input.Keys.NUM_9) {
            if (keyCode == Input.Keys.NUM_0 && this.selectInput.isEmpty()) return;
            this.selectInput += keyCode - Input.Keys.NUM_0;
        }
    }

    private void reset() {
        this.yLeft = 0;
        this.yRight = 0;
    }

    public void left(Renderer renderer, String text, Object o) {
        this.left(renderer, TextObject.literal(text), o);
    }

    public void left(Renderer renderer, MutableText text, Object o) {
        FormatterContext formatterContext = new FormatterContext();
        DebugRenderer.format(o, formatterContext);
        this.left(renderer, text.append(TextObject.literal(": ")).append(formatterContext.build()));
    }

    public void left(Renderer renderer, String text) {
        this.left(renderer, TextObject.literal(text));
    }

    public void left(Renderer renderer, MutableText text) {
        String text1 = text.getText();
        this.layout.setText(this.font.get(), text1);
        Vector2 bounds = new Vector2(this.layout.width, this.font.get().getLineHeight());
        int width = (int) bounds.x;
        int height = (int) bounds.y;
        int y = this.yLeft += height + 5;
        if (this.game.isInGame()) {
            y += (int) this.game.getGameBounds().y;
        }

        renderer.fill(10, y, width + 4, height + 4, Color.BLACK.withAlpha(0x99));
        renderer.drawText(this.font.get(), text1, 12, y + 1, Color.WHITE);
    }

    public void right(Renderer renderer, String text, Object o) {
        this.right(renderer, TextObject.literal(text), o);
    }

    public void right(Renderer renderer, MutableText text, Object o) {
        FormatterContext formatterContext = new FormatterContext();
        DebugRenderer.format(o, formatterContext);
        this.right(renderer, text.append(TextObject.literal(": ")).append(formatterContext.build()));
    }

    public void right(Renderer renderer, String text) {
        this.right(renderer, TextObject.literal(text));
    }

    public void right(Renderer renderer, MutableText text) {
        String text1 = text.getText();
        this.layout.setText(this.font.get(), text1);
        Vector2 bounds = new Vector2(this.layout.width, this.font.get().getLineHeight());
        int width = (int) bounds.x;
        int height = (int) bounds.y;
        int y = this.yRight += height + 5;
        if (this.game.isInGame()) {
            y += (int) this.game.getGameBounds().y;
        }

        renderer.fill(this.game.getWidth() - width - 10, y, width + 4, height + 4, Color.BLACK.withAlpha(0x99));
        renderer.drawTextRight(this.font.get(), text1, this.game.getWidth() - 8, y + 1, Color.WHITE);
    }

    public void right(Renderer renderer, MutableText text, MutableText text1) {
        String text2 = text.getText();
        this.layout.setText(this.font.get(), text2);
        Vector2 bounds = new Vector2(this.layout.width, this.font.get().getLineHeight());
        int height;
        if (!text1.getText().isEmpty()) {
            Vector2 bounds1 = new Vector2(this.layout.width, this.font.get().getLineHeight());
            height = Math.max((int) bounds.y, (int) bounds1.y);
        } else {
            height = (int) bounds.y;
        }
        int y = this.yRight += height + 5;
        if (this.game.isInGame()) {
            y += (int) this.game.getGameBounds().y;
        }

        int i = 400;

        renderer.fill(this.game.getWidth() - i - 10, y, i, height + 4, Color.BLACK.withAlpha(0x99));
        renderer.drawTextRight(this.font.get(), text2, this.game.getWidth() - i - 8, y + 1, Color.WHITE);
        if (!text1.getText().isEmpty()) {
            renderer.drawTextRight(this.font.get(), text2, this.game.getWidth() - 12, y + 1, Color.WHITE);
        }
    }
}
