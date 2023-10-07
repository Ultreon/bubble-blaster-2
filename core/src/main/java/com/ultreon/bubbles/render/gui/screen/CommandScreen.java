package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.ChatMessage;
import com.ultreon.bubbles.command.CommandConstructor;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;

import java.time.Instant;
import java.util.*;

public class CommandScreen extends Screen {
    public static final int MAX_MESSAGES = 50;
    public static final int MESSAGE_DURATION = 3000;

    private static final Deque<ChatMessage> MESSAGES = new ArrayDeque<>();

    private final BitmapFont font = Fonts.MONOSPACED_14.get();
    private final GlyphLayout layout = new GlyphLayout();
    private final GlyphLayout beginLayout = new GlyphLayout();
    private String currentText = "/";
    private int cursorIndex = 1;
    private boolean firstSymbol = true;
    private final BitmapFont textFont = Fonts.SANS_REGULAR_24.get();

    public CommandScreen() {
        super();
    }

    public static void addMessage(String message) {
        CommandScreen.addMessage(message, false);
    }

    public static void addMessage(String message, boolean system) {
        var lines = message.replaceAll("\r\n", "\n").replaceAll("\r", "\n").split("\n");
        for (var line : lines)
            CommandScreen.addMessageLine(line, system);
    }

    public static void addMessageLine(String message) {
        CommandScreen.addMessageLine(message, false);
    }

    public static void addMessageLine(String message, boolean system) {
        var line = message.replaceAll("[\r\n]", "");
        if (line.isBlank()) return;

        MESSAGES.addFirst(new ChatMessage(line, system));
        if (MESSAGES.size() > MAX_MESSAGES)
            MESSAGES.removeLast();
    }

    /**
     * Renders the hud, in this method only the system and chat messages.
     * Should not being called, for internal use only.
     *
     * @param renderer a 2D graphics instance.
     */
    public static void drawMessages(Renderer renderer) {
        CommandScreen.drawMessages(renderer, 71);
    }

    /**
     * Renders the hud, in this method only the system and chat messages.
     * Should not being called, for internal use only.
     *
     * @param renderer a 2D graphics instance.
     * @param y y offset
     */
    public static void drawMessages(Renderer renderer, int y) {
        CommandScreen.drawMessages(renderer, y, false);
    }

    /**
     * Renders the hud, in this method only the system and chat messages.
     * Should not being called, for internal use only.
     *
     * @param renderer  a 2D graphics instance.
     */
    public static void drawMessages(Renderer renderer, int y, boolean all) {
        var line = 1;
        for (var message : MESSAGES) {
            var msgY = y - 22 * line;
            if (msgY < -20) break;
            if (!all && Instant.now().isAfter(message.created().plusMillis(MESSAGE_DURATION))) continue;
            CommandScreen.drawMessage(renderer, msgY, message);
            line++;
        }
    }

    private static void drawMessage(Renderer renderer, int y, ChatMessage message) {
        renderer.fill(2, y, 1000, 20, Color.BLACK.withAlpha(0x80));

        renderer.scissored(2, y, 1000, 20, () -> renderer.drawText(Fonts.SANS_REGULAR_16.get(), message.text(), 4, y + 2, message.system() ? Color.YELLOW.brighter() : Color.WHITE));
    }

    @Override
    public void init() {
        this.firstSymbol = true;
        this.revalidate();
    }

    @Override
    public boolean close(Screen to) {
        this.currentText = "/";
        this.cursorIndex = 1;

        return super.close(to);
    }

    @Override
    public boolean charType(char character) {
        if (super.charType(character)) return true;
        if (this.firstSymbol) return this.firstSymbol = false;

        if ((int) character >= 0x20) {
            this.currentText += character;
            this.cursorIndex++;
            this.revalidate();
            return true;
        }
        return false;
    }

    private void revalidate() {
        this.layout.setText(this.textFont, this.currentText);
        this.beginLayout.setText(this.textFont, this.currentText.substring(0, this.cursorIndex));
    }

    @Override
    public boolean keyPress(int keyCode) {
        if (super.keyPress(keyCode)) return true;

        if (keyCode == Input.Keys.BACKSPACE && !this.currentText.isEmpty() && this.cursorIndex > 0) {
            this.currentText = this.currentText.substring(0, this.currentText.length() - 1);
            this.cursorIndex--;
            this.revalidate();
            return true;
        }
        if (keyCode == Input.Keys.LEFT && this.cursorIndex > 0) {
            this.cursorIndex--;
            this.revalidate();
            return true;
        }
        if (keyCode == Input.Keys.RIGHT && this.cursorIndex < this.currentText.length()) {
            this.cursorIndex++;
            this.revalidate();
            return true;
        }
        if (keyCode == Input.Keys.ENTER) {
            var loadedGame = BubbleBlaster.getInstance().getLoadedGame();
            if (loadedGame != null) {
                if (!this.currentText.isEmpty()) {
                    if (this.currentText.charAt(0) != '/') {
                        CommandScreen.addMessage("[Player]: " + this.currentText);
                    } else {
                        var parsed = Arrays.asList(CommandScreen.translateCommandline(this.currentText.substring(1)));
                        if (!parsed.isEmpty()) {
                            var args = parsed.subList(1, parsed.size()).toArray(new String[]{});

                            BubbleBlaster.invokeTick(() -> {
                                if (!CommandConstructor.execute(parsed.get(0), loadedGame.getGamemode().getPlayer(), args)) {
                                    CommandScreen.addMessage("Command ‘" + parsed.get(0) + "’ is non-existent.", true);
                                    BubbleBlaster.getInstance().showScreen(null);
                                }
                            });
                        }
                    }
                }
                BubbleBlaster.getInstance().showScreen(null);
                return true;
            }
        }
        return false;
    }

    /**
     * Crack a command line.
     *
     * @param toProcess the command line to process.
     * @return the command line broken into strings.
     * An empty or null toProcess parameter results in a zero sized array.
     */
    public static String[] translateCommandline(String toProcess) {
        if (toProcess == null || toProcess.isEmpty()) {
            //no command? no string
            return new String[0];
        }
        // parse with a simple finite state machine

        final var normal = 0;
        final var inQuote = 1;
        final var inDoubleQuote = 2;
        var state = normal;
        final var tok = new StringTokenizer(toProcess, "\"' ", true);
        final var result = new ArrayList<String>();
        final var current = new StringBuilder();
        var lastTokenHasBeenQuoted = false;

        while (tok.hasMoreTokens()) {
            var nextTok = tok.nextToken();
            switch (state) {
                case inQuote:
                    if ("'".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                case inDoubleQuote:
                    if ("\"".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                default:
                    if ("'".equals(nextTok)) {
                        state = inQuote;
                    } else if ("\"".equals(nextTok)) {
                        state = inDoubleQuote;
                    } else if (" ".equals(nextTok)) {
                        if (lastTokenHasBeenQuoted || !current.toString().isEmpty()) {
                            result.add(current.toString());
                            current.setLength(0);
                        }
                    } else {
                        current.append(nextTok);
                    }
                    lastTokenHasBeenQuoted = false;
                    break;
            }
        }
        if (lastTokenHasBeenQuoted || !current.toString().isEmpty()) {
            result.add(current.toString());
        }
        return result.toArray(new String[0]);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        renderer.setColor(Color.argb(0x40000000));
        renderer.fill(0, 0, this.width, this.height, Color.BLACK.withAlpha(0x40));
        renderer.fill(0, this.height - 32, BubbleBlaster.getInstance().getWidth(), 32, Color.BLACK.withAlpha(0x80));

        CommandScreen.drawMessages(renderer, this.height - 36, true);

        renderer.drawText(this.textFont, this.currentText, 4, this.height - 26, Color.WHITE);

        float cursorX;
        if (this.cursorIndex >= this.currentText.length()) {
            if (!this.currentText.isEmpty()) {
                cursorX = this.beginLayout.width + 2;
            } else {
                cursorX = 0;
            }

            renderer.fillEffect(cursorX, this.height - 30, 2, 28);
        } else {
            if (!this.currentText.isEmpty()) {
                cursorX = this.beginLayout.width;
            } else {
                cursorX = 0;
            }

            var width = this.font.getData().getGlyph(this.currentText.charAt(this.cursorIndex)).width;
            renderer.fillEffect(cursorX, this.height - 2, width, 2);
        }
    }

    @Override
    public boolean doesPauseGame() {
        return false;
    }

}
