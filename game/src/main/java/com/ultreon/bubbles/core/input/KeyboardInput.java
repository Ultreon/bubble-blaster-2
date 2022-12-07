package com.ultreon.bubbles.core.input;

import com.ultreon.bubbles.event.v2.InputEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.input.KeyInput;
import com.ultreon.bubbles.render.screen.PauseScreen;
import com.ultreon.bubbles.render.screen.Screen;
import com.ultreon.bubbles.render.screen.ScreenManager;
import org.checkerframework.checker.nullness.qual.EnsuresKeyForIf;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @see MouseInput
 * @see java.awt.event.KeyAdapter
 */
public abstract class KeyboardInput extends KeyAdapter {
    private static final Set<Integer> keysDown = new CopyOnWriteArraySet<>();
    @NonNull
    private final BubbleBlaster game;

    private static final int SHIFT_MASK = 1;
    private static final int CTRL_MASK = 1 << 1;
    private static final int META_MASK = 1 << 2;
    private static final int ALT_MASK = 1 << 3;
    private static final int ALT_GRAPH_MASK = 1 << 5;

    private static final int BUTTON1_MASK = 1 << 4;
    private static final int BUTTON2_MASK = 1 << 3;
    private static final int BUTTON3_MASK = 1 << 2;

    public static boolean isShiftDown(int modifiers) {
        return (modifiers & SHIFT_MASK) != 0;
    }

    public static boolean isCtrlDown(int modifiers) {
        return (modifiers & CTRL_MASK) != 0;
    }

    public static boolean isMetaDown(int modifiers) {
        return (modifiers & META_MASK) != 0;
    }

    public static boolean isAltDown(int modifiers) {
        return (modifiers & ALT_MASK) != 0;
    }

    public static boolean isAltGraphDown(int modifiers) {
        return (modifiers & ALT_GRAPH_MASK) != 0;
    }

    @EnsuresNonNull("game")
    public KeyboardInput() {
        this.game = BubbleBlaster.getInstance();
    }

    @EnsuresKeyForIf(result = true, expression = "keyCode", map = "keysDown")
    public static boolean isDown(int keyCode) {
        return keysDown.contains(keyCode);
    }

    @Override
    public final void keyPressed(KeyEvent e) {
        if (isDown(e.getKeyCode())) {
            InputEvents.KEY_PRESS.factory().onKeyPress(e.getExtendedKeyCode(), e.getKeyLocation(), e.getModifiersEx(), true);
        } else {
            BubbleBlaster.getInstance().keyPress(e.getExtendedKeyCode(), e.getKeyLocation(), e.getModifiersEx());
            InputEvents.KEY_PRESS.factory().onKeyPress(e.getExtendedKeyCode(), e.getKeyLocation(), e.getModifiersEx(), false);
            keysDown.add(e.getKeyCode());
        }

        ScreenManager screenManager = this.game.getScreenManager();
        Screen currentScreen = screenManager.getCurrentScreen();
        if (currentScreen != null) {
            currentScreen.keyPress(e.getKeyCode(), e.getKeyChar());
        } else if (e.getKeyCode() == KeyInput.Map.KEY_ESCAPE) {
            BubbleBlaster.getInstance().showScreen(new PauseScreen());
        }
    }

    @Override
    public final void keyReleased(KeyEvent e) {
        keysDown.remove(e.getKeyCode());
        InputEvents.KEY_RELEASE.factory().onKeyRelease(e.getExtendedKeyCode(), e.getKeyLocation(), e.getModifiersEx());

        ScreenManager screenManager = this.game.getScreenManager();
        Screen currentScreen = screenManager.getCurrentScreen();
        if (currentScreen != null) {
            currentScreen.keyRelease(e.getKeyCode(), e.getKeyChar());
        }
    }

    @Override
    public final void keyTyped(KeyEvent e) {
        InputEvents.CHAR_TYPE.factory().onCharType(e.getKeyChar());

        ScreenManager screenManager = this.game.getScreenManager();
        Screen currentScreen = screenManager.getCurrentScreen();
        if (currentScreen != null) {
            currentScreen.charType(e.getKeyCode(), e.getKeyChar());
        }
    }
}
