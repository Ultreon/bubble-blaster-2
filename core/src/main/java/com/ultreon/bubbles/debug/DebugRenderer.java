package com.ultreon.bubbles.debug;

import com.google.common.collect.Lists;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v1.InputEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.font.Font;
import com.ultreon.bubbles.render.font.SystemFont;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.libs.text.v0.MutableText;
import com.ultreon.libs.text.v0.TextObject;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.*;

public class DebugRenderer {
    private static final int FONT_SIZE = 13;
    private static final Formatter<Object> DEFAULT_FORMATTER = new Formatter<>(Object.class, BubbleBlaster.id("object")) {
        @Override
        public void format(Object obj, IFormatterContext context) {
            Class<?> c = obj.getClass();

            context.classValue(c);
            context.identifier("@");
            context.hexValue(obj.hashCode());
        }
    };
    private final Font font;
    private final BubbleBlaster game;
    private int yLeft;
    private int yRight;
    private Thread selectedThread = null;
    private Map<Thread, ThreadSection> lastProfile;
    private String selectInput = "";
    private final Stack<String> pathStack = new Stack<>();
    private final Stack<Section> sectionStack = new Stack<>();

    public DebugRenderer(BubbleBlaster game) {
        this.game = game;
        font = new SystemFont("monospaced");

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
        reset();

//        renderer.font(font);
        renderer.color(255, 255, 255);

        Rectangle gameBounds = game.getGameBounds();
        left(renderer, "FPS", game.getFps());
        left(renderer, "TPS", game.getCurrentTps());
        left(renderer, "RPT", game.getGameFrameTime());
        left(renderer, "Game Bounds", gameBounds);
        left(renderer, "Scaled Size", new Dimension(game.getScaledWidth(), game.getScaledHeight()));
        left(renderer, "Window Size", new Dimension(game.getGameWindow().getWidth(), game.getGameWindow().getHeight()));
        left(renderer, "Canvas Size", new Dimension(game.getWidth(), game.getHeight()));
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

            if (KeyboardInput.isDown(KeyboardInput.Map.KEY_SHIFT)) {
                Entity entityAt = environment.getEntityAt(MouseInput.getPos());
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
        left(renderer, "Screen: ", (screen == null ? null : screen.getClass()));

        lastProfile = BubbleBlaster.getLastProfile();
        if (selectedThread == null) {
            List<Thread> threads = Lists.newArrayList(lastProfile.keySet());
            threads.sort(Comparator.comparing(Thread::getName));

            MutableText selInputText = TextObject.literal(selectInput);
            selInputText.setColor(new Color(0xff4040));
            MutableText typingText = TextObject.literal("Typing ");
            typingText.setColor(new Color(0xffa040));
            right(renderer, typingText, selInputText);
//            right(renderer, "-----------------");

            for (int i = 0, threadsSize = threads.size(); i < threadsSize; i++) {
                Thread e = threads.get(i);
                MutableText literal = TextObject.literal(" [" + e.getName() + "]");
                literal.setColor(new Color(0xff4040));
                MutableText thread = TextObject.literal("Thread");
                MutableText index = TextObject.literal("[" + (i + 1) + "] ");
                index.setColor(new Color(0x30ff30));
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
            curMillisText.setColor(new Color(0xff4040));
            MutableText curSectionText = TextObject.literal("Current Section: ");
            curSectionText.setColor(new Color(0xffa040));
            right(renderer, curSectionText, curMillisText);
            MutableText selInputText = TextObject.literal(selectInput);
            selInputText.setColor(new Color(0xff4040));
            MutableText typingText = TextObject.literal("Typing ");
            typingText.setColor(new Color(0xffa040));
            right(renderer, typingText, selInputText);
//            right(renderer, "-----------------");
            for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
                Map.Entry<String, Section> e = entries.get(i);
                MutableText millisText = TextObject.literal(" (" + e.getValue().getMillis() + "ms)");
                millisText.setColor(new Color(0xff4040));
                MutableText name = TextObject.literal(e.getKey());
                MutableText index = TextObject.literal("[" + (i + 1) + "] ");
                index.setColor(new Color(0x30ff30));
                right(renderer, index.append(name), millisText);
            }
        }
    }

    private void keyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyboardInput.Map.KEY_ENTER) {
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
        } else if (keyCode == KeyboardInput.Map.KEY_DELETE) {
            if (selectedThread == null) return;
            if (pathStack.isEmpty()) return;
            sectionStack.pop();
            pathStack.pop();
        } else if (keyCode == KeyboardInput.Map.KEY_BACK_SPACE) {
            if (selectInput.isEmpty()) return;
            selectInput = selectInput.substring(0, selectInput.length() - 1);
        } else if (keyCode >= KeyboardInput.Map.KEY_0 && keyCode <= KeyboardInput.Map.KEY_9) {
            if (keyCode == KeyboardInput.Map.KEY_0 && selectInput.isEmpty()) return;
            selectInput += keyCode - KeyboardInput.Map.KEY_0;
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
        Rectangle2D.Float bounds = font.bounds(renderer, FONT_SIZE, text);
        int height = (int) bounds.height;
        int width = (int) bounds.width;
        int y = yLeft += height + 5;
        if (game.isInGame()) {
            y += game.getGameBounds().y;
        }

        renderer.color(0, 0, 0, 0x99);
        renderer.rect(10, y, width + 4, height + 4);
        renderer.color("#fff");
        font.draw(renderer, text, FONT_SIZE, 12, y + 1);
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
        Rectangle2D.Float bounds = font.bounds(renderer, FONT_SIZE, text);
        int height = (int) bounds.height;
        int width = (int) bounds.width;
        int y = yRight += height + 5;
        if (game.isInGame()) {
            y += game.getGameBounds().y;
        }

        renderer.color(0, 0, 0, 0x99);
        renderer.rect(game.getWidth() - width - 10, y, width + 4, height + 4);
        renderer.color("#fff");
        font.draw(renderer, text, FONT_SIZE, game.getWidth() - 8, y + 1, Anchor.NW);
    }

    public void right(Renderer renderer, MutableText text, MutableText text1) {
        Rectangle2D.Float bounds = font.bounds(renderer, FONT_SIZE, text);
        int height;
        if (!text1.getText().isEmpty()) {
            Rectangle2D.Float bounds1 = font.bounds(renderer, FONT_SIZE, text1);
            height = Math.max((int) bounds.height, (int) bounds1.height);
        } else {
            height = (int) bounds.height;
        }
        int y = yRight += height + 5;
        if (game.isInGame()) {
            y += game.getGameBounds().y;
        }

        int i = 400;

        renderer.color(0, 0, 0, 0x99);
        renderer.rect(game.getWidth() - i - 10, y, i, height + 4);
        renderer.color("#fff");
        font.draw(renderer, text, FONT_SIZE, game.getWidth() - i - 8, y + 1, Anchor.NW);
        if (!text1.getText().isEmpty()) {
            font.draw(renderer, text1, FONT_SIZE, game.getWidth() - 12, y + 1, Anchor.NE);
        }
    }
}
