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
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v1.InputEvents;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.input.GameInput;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.libs.commons.v0.size.FloatSize;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.registries.v0.RegistrySupplier;
import com.ultreon.libs.text.v0.MutableText;
import com.ultreon.libs.text.v0.TextObject;

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
        if (game.isLoading()) return;

        reset();

        renderer.setColor(255, 255, 255);

        Rectangle gameBounds = game.getGameBounds();
        left(renderer, "FPS", game.getFps());
        left(renderer, "TPS", game.getCurrentTps());
        left(renderer, "RPT", game.getGameFrameTime());
        left(renderer, "Game Bounds", gameBounds);
        left(renderer, "Scaled Size", new FloatSize(game.getScaledWidth(), game.getScaledHeight()));
        left(renderer, "Window Size", new FloatSize(game.getGameWindow().getWidth(), game.getGameWindow().getHeight()));
        left(renderer, "Canvas Size", new FloatSize(game.getWidth(), game.getHeight()));
        Environment environment = game.environment;
        if (environment != null) {
            GameplayEvent curGe = environment.getCurrentGameEvent();
            left(renderer, "Entity Count", environment.getEntities().size());
            left(renderer, "Visible Entity Count", environment.getEntities().stream().filter(Entity::isVisible).count());
            left(renderer, "Entity Removal Count", environment.getEntities().stream().filter(Entity::willBeDeleted).count());
            left(renderer, "Cur. Game Event", (curGe != null ? Registries.GAMEPLAY_EVENTS.getKey(curGe) : null));
            left(renderer, "Is Initialized", environment.isInitialized());
            left(renderer, "Difficulty", environment.getDifficulty().name());
            left(renderer, "Local Difficulty", environment.getLocalDifficulty());
            left(renderer, "Seed", environment.getSeed());

            if (GameInput.isKeyDown(Input.Keys.SHIFT_LEFT)) {
                GridPoint2 pos = GameInput.getPos();
                Entity entityAt = environment.getEntityAt(new Vec2i(pos.x, pos.y));
                if (entityAt != null) {
                    left(renderer, "Entity Type", Registries.ENTITIES.getKey(entityAt.getType()));
                    if (entityAt instanceof Bubble bubble) {
                        left(renderer, "Bubble Type", Registries.BUBBLES.getKey(bubble.getBubbleType()));
                        left(renderer, "Base Speed:", bubble.getAttributes().getBase(Attribute.SPEED));
                        left(renderer, "Speed", bubble.getAttributes().get(Attribute.SPEED));
                        left(renderer, "Radius", bubble.getRadius());
                    }
                }
            }

            Player player = environment.getPlayer();
            if (player != null) {
                left(renderer, "Pos", player.getPos());
                left(renderer, "Score", player.getScore());
                left(renderer, "Level", player.getLevel());
                left(renderer, "Speed", player.getSpeed());
                left(renderer, "Rotation", player.getRotation());
                left(renderer, "Rot Speed", player.getRotationSpeed());
            }
        }
        Screen screen = game.getCurrentScreen();
        left(renderer, "Screen", (screen == null ? null : screen.getClass()));

        lastProfile = BubbleBlaster.getLastProfile();
        if (selectedThread == null) {
            List<Thread> threads = Lists.newArrayList(lastProfile.keySet());
            threads.sort(Comparator.comparing(Thread::getName));

            MutableText selInputText = TextObject.literal(selectInput);
            selInputText.setColor(Color.rgb(0xff4040).toAwt());
            MutableText typingText = TextObject.literal("Typing ");
            typingText.setColor(Color.rgb(0xffa040).toAwt());
            right(renderer, typingText, selInputText);
//            right(renderer, "-----------------");

            for (int i = 0, threadsSize = threads.size(); i < threadsSize; i++) {
                Thread e = threads.get(i);
                MutableText literal = TextObject.literal(" [" + e.getName() + "]");
                literal.setColor(Color.rgb(0xff4040).toAwt());
                MutableText thread = TextObject.literal("Thread");
                MutableText index = TextObject.literal("[" + (i + 1) + "] ");
                index.setColor(Color.rgb(0x30ff30).toAwt());
                right(renderer, index.append(thread), literal);
            }
        } else {
            ArrayList<Map.Entry<String, Section>> entries;
            long millis;

            if (sectionStack.isEmpty()) {
                entries = Lists.newArrayList(lastProfile.get(selectedThread).getValues().entrySet());
                entries.sort(Map.Entry.comparingByKey());
                millis = entries.stream().mapToLong(e -> e.getValue().getMillis()).sum();
            } else {
                Section peek = sectionStack.peek();
                millis = peek.getMillis();

                entries = Lists.newArrayList(peek.getValues().entrySet());
                entries.sort(Map.Entry.comparingByKey());
            }

            MutableText curMillisText = TextObject.literal(" (" + millis + "ms)");
            curMillisText.setColor(Color.rgb(0xff4040).toAwt());
            MutableText curSectionText = TextObject.literal("Current Section: ");
            curSectionText.setColor(Color.rgb(0xffa040).toAwt());
            right(renderer, curSectionText, curMillisText);
            MutableText selInputText = TextObject.literal(selectInput);
            selInputText.setColor(Color.rgb(0xff4040).toAwt());
            MutableText typingText = TextObject.literal("Typing ");
            typingText.setColor(Color.rgb(0xffa040).toAwt());
            right(renderer, typingText, selInputText);
//            right(renderer, "-----------------");
            for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
                Map.Entry<String, Section> e = entries.get(i);
                MutableText millisText = TextObject.literal(" (" + e.getValue().getMillis() + "ms)");
                millisText.setColor(Color.rgb(0xff4040).toAwt());
                MutableText name = TextObject.literal(e.getKey());
                MutableText index = TextObject.literal("[" + (i + 1) + "] ");
                index.setColor(Color.rgb(0x30ff30).toAwt());
                right(renderer, index.append(name), millisText);
            }
        }
    }

    private void keyPress(int keyCode) {
        if (keyCode == Input.Keys.ENTER) {
            System.out.println("selectInput = " + selectInput);
            if (selectInput.isEmpty()) return;
            int number;
            try {
                number = Integer.parseInt(selectInput);
            } catch (NumberFormatException e) {
                return;
            }
            selectInput = "";
            System.out.println("number = " + number);
            number--;
            if (selectedThread == null) {
                List<Thread> threads = Lists.newArrayList(lastProfile.keySet());

                if (number >= threads.size()) return;

                threads.sort(Comparator.comparing(Thread::getName));
                selectedThread = threads.get(number);
                System.out.println("selectedThread = " + selectedThread);
            } else {
                ArrayList<Map.Entry<String, Section>> entries;

                if (sectionStack.isEmpty()) {
                    entries = Lists.newArrayList(lastProfile.get(selectedThread).getValues().entrySet());
                    entries.sort(Map.Entry.comparingByKey());
                } else {
                    Section peek = sectionStack.peek();

                    entries = Lists.newArrayList(peek.getValues().entrySet());
                    entries.sort(Map.Entry.comparingByKey());
                }

                if (number >= entries.size()) return;

                var entry = entries.get(number);
                sectionStack.push(entry.getValue());
                pathStack.push(entry.getKey());
                System.out.println("entry = " + entry.getKey());
            }
        } else if (keyCode == Input.Keys.FORWARD_DEL) {
            if (selectedThread == null) return;
            if (pathStack.isEmpty()) return;
            sectionStack.pop();
            pathStack.pop();
        } else if (keyCode == Input.Keys.BACKSPACE) {
            if (selectInput.isEmpty()) return;
            selectInput = selectInput.substring(0, selectInput.length() - 1);
        } else if (keyCode >= Input.Keys.NUM_0 && keyCode <= Input.Keys.NUM_9) {
            if (keyCode == Input.Keys.NUM_0 && selectInput.isEmpty()) return;
            selectInput += keyCode - Input.Keys.NUM_0;
        }
    }

    private void reset() {
        yLeft = 0;
        yRight = 0;
    }

    public void left(Renderer renderer, String text, Object o) {
        left(renderer, TextObject.literal(text), o);
    }

    public void left(Renderer renderer, MutableText text, Object o) {
        FormatterContext formatterContext = new FormatterContext();
        format(o, formatterContext);
        left(renderer, text.append(TextObject.literal(": ")).append(formatterContext.build()));
    }

    public void left(Renderer renderer, String text) {
        left(renderer, TextObject.literal(text));
    }

    public void left(Renderer renderer, MutableText text) {
        String text1 = text.getText();
        layout.setText(font.get(), text1);
        Vector2 bounds = new Vector2(layout.width, font.get().getLineHeight());
        int width = (int) bounds.x;
        int height = (int) bounds.y;
        int y = yLeft += height + 5;
        if (game.isInGame()) {
            y += (int) game.getGameBounds().y;
        }

        renderer.setColor(0, 0, 0, 0x99);
        renderer.rect(10, y, width + 4, height + 4);
        renderer.setColor("#fff");
        renderer.drawText(font.get(), text1, 12, y + 1);
    }

    public void right(Renderer renderer, String text, Object o) {
        right(renderer, TextObject.literal(text), o);
    }

    public void right(Renderer renderer, MutableText text, Object o) {
        FormatterContext formatterContext = new FormatterContext();
        format(o, formatterContext);
        right(renderer, text.append(TextObject.literal(": ")).append(formatterContext.build()));
    }

    public void right(Renderer renderer, String text) {
        right(renderer, TextObject.literal(text));
    }

    public void right(Renderer renderer, MutableText text) {
        String text1 = text.getText();
        layout.setText(font.get(), text1);
        Vector2 bounds = new Vector2(layout.width, font.get().getLineHeight());
        int width = (int) bounds.x;
        int height = (int) bounds.y;
        int y = yRight += height + 5;
        if (game.isInGame()) {
            y += (int) game.getGameBounds().y;
        }

        renderer.setColor(0, 0, 0, 0x99);
        renderer.rect(game.getWidth() - width - 10, y, width + 4, height + 4);
        renderer.setColor("#fff");
        renderer.drawRightAnchoredText(font.get(), text1, game.getWidth() - 8, y + 1);
    }

    public void right(Renderer renderer, MutableText text, MutableText text1) {
        String text2 = text.getText();
        layout.setText(font.get(), text2);
        Vector2 bounds = new Vector2(layout.width, font.get().getLineHeight());
        int height;
        if (!text1.getText().isEmpty()) {
            Vector2 bounds1 = new Vector2(layout.width, font.get().getLineHeight());
            height = Math.max((int) bounds.y, (int) bounds1.y);
        } else {
            height = (int) bounds.y;
        }
        int y = yRight += height + 5;
        if (game.isInGame()) {
            y += (int) game.getGameBounds().y;
        }

        int i = 400;

        renderer.setColor(0, 0, 0, 0x99);
        renderer.rect(game.getWidth() - i - 10, y, i, height + 4);
        renderer.setColor("#fff");
        renderer.drawRightAnchoredText(font.get(), text2, game.getWidth() - i - 8, y + 1);
        if (!text1.getText().isEmpty()) {
            renderer.drawRightAnchoredText(font.get(), text2, game.getWidth() - 12, y + 1);
        }
    }
}
