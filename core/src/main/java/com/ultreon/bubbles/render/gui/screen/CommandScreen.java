package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.command.CommandConstructor;
import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.LoadedGame;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.util.Util;
import com.ultreon.bubbles.vector.Vec2i;

import java.awt.*;
import java.util.List;
import java.util.*;

public class CommandScreen extends Screen {
    private final Font defaultFont = new Font(Font.MONOSPACED, Font.BOLD, 30);
    private String currentText = "/";
    private int cursorIndex = 1;

    public CommandScreen() {
        super();
    }

    @Override
    public void init() {

    }

    @Override
    public boolean onClose(Screen to) {
        currentText = "/";
        cursorIndex = 1;

        return super.onClose(to);
    }

    @Override
    public boolean charType(int keyCode, char character) {
        if (super.charType(keyCode, character)) return true;

        if ((int) character >= 0x20) {
            currentText += character;
            cursorIndex++;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPress(int keyCode, char character) {
        if (super.keyPress(keyCode, character)) return true;

        if (keyCode == KeyboardInput.Map.KEY_BACK_SPACE && !currentText.isEmpty()) {
            currentText = currentText.substring(0, currentText.length() - 1);
            cursorIndex--;
            return true;
        }
        if (keyCode == KeyboardInput.Map.KEY_LEFT && cursorIndex > 0) {
            cursorIndex--;
            return true;
        }
        if (keyCode == KeyboardInput.Map.KEY_RIGHT && cursorIndex < currentText.length() - 1) {
            cursorIndex++;
            return true;
        }
        if (keyCode == KeyboardInput.Map.KEY_ENTER) {
            LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
            if (loadedGame != null) {
                if (!currentText.isEmpty()) {
                    if (currentText.charAt(0) != '/') {
                        BubbleBlaster.getLogger().debug("Not a command: " + currentText);
                        Objects.requireNonNull(loadedGame.getGamemode().getPlayer()).sendMessage("Not a command, start with a ‘/’ for a command.");
                    } else {
                        List<String> parsed = Arrays.asList(translateCommandline(currentText.substring(1)));
                        if (!parsed.isEmpty()) {
                            String[] args = parsed.subList(1, parsed.size()).toArray(new String[]{});

                            if (!CommandConstructor.execute(parsed.get(0), loadedGame.getGamemode().getPlayer(), args)) {
                                Objects.requireNonNull(loadedGame.getGamemode().getPlayer()).sendMessage("Command ‘" + parsed.get(0) + "’ is non-existent.");
                                BubbleBlaster.getInstance().showScreen(null);
                            }
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

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        final StringTokenizer tok = new StringTokenizer(toProcess, "\"' ", true);
        final ArrayList<String> result = new ArrayList<>();
        final StringBuilder current = new StringBuilder();
        boolean lastTokenHasBeenQuoted = false;

        while (tok.hasMoreTokens()) {
            String nextTok = tok.nextToken();
            switch (state) {
                case inQuote -> {
                    if ("'".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                }
                case inDoubleQuote -> {
                    if ("\"".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                }
                default -> {
                    if ("'".equals(nextTok)) {
                        state = inQuote;
                    } else if ("\"".equals(nextTok)) {
                        state = inDoubleQuote;
                    } else if (" ".equals(nextTok)) {
                        if (lastTokenHasBeenQuoted || current.length() > 0) {
                            result.add(current.toString());
                            current.setLength(0);
                        }
                    } else {
                        current.append(nextTok);
                    }
                    lastTokenHasBeenQuoted = false;
                }
            }
        }
        if (lastTokenHasBeenQuoted || current.length() > 0) {
            result.add(current.toString());
        }
        return result.toArray(new String[0]);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        renderer.color(Color.argb(0x40000000));
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), height);

        renderer.color(Color.argb(0x80000000));
        renderer.rect(0, height - 32, BubbleBlaster.getInstance().getWidth(), 32);

        renderer.color(Color.argb(0xffffffff));
        GraphicsUtils.drawLeftAnchoredString(renderer, currentText, new Vec2i(2, height - 28), 28, defaultFont);

        FontMetrics fontMetrics = renderer.fontMetrics(defaultFont);


        int cursorX;
        renderer.color(Color.argb(0xff0090c0));
        if (cursorIndex >= currentText.length()) {
            if (currentText.length() != 0) {
                cursorX = fontMetrics.stringWidth(currentText.substring(0, cursorIndex)) + 2;
            } else {
                cursorX = 0;
            }

            renderer.line(cursorX, height - 30, cursorX, height - 2);
            renderer.line(cursorX + 1, height - 30, cursorX + 1, height - 2);
        } else {
            if (currentText.length() != 0) {
                cursorX = fontMetrics.stringWidth(currentText.substring(0, cursorIndex));
            } else {
                cursorX = 0;
            }

            int width = fontMetrics.charWidth(currentText.charAt(cursorIndex));
            renderer.line(cursorX, height - 2, cursorX + width, height - 2);
            renderer.line(cursorX, height - 1, cursorX + width, height - 1);
        }
    }
}
