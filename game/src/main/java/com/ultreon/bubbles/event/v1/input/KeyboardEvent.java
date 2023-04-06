package com.ultreon.bubbles.event.v1.input;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.event.v1.Event;
import com.ultreon.bubbles.event.v1.type.KeyEventType;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * Keyboard Event
 * This event is used for handling keyboard input.
 *
 * @see KeyboardEvent
 * @see KeyboardInput
 * @see KeyEvent
 * @see KeyEventType
 */
@Deprecated
public class KeyboardEvent extends Event {
    private final int extendedKeyCode;
    private final int keyCode;
    private final char keyChar;
    private final int modifiers;
    private final int keyLocation;
    private final long when;

    private final BubbleBlaster main;
    private final KeyboardInput controller;
    private final KeyEvent parentEvent;
    private final KeyEventType type;
    private final HashMap<Integer, Boolean> pressed = new HashMap<>();

    /**
     * Keyboard event, called from a specific scene.
     *
     * @param main       The {@linkplain BubbleBlaster Bubble Blaster} instance.
     * @param controller The {@linkplain KeyboardInput keyboard input} instance.
     * @param event      The {@linkplain KeyEvent keyboard event} instance.
     * @param type       One create the {@linkplain KeyEventType keyboard event type} constants.
     */
    public KeyboardEvent(BubbleBlaster main, @NotNull KeyboardInput controller, KeyEvent event, KeyEventType type) {
        this.main = main;
        this.type = type;
        this.controller = controller;
        this.parentEvent = event;
        this.keyCode = event.getKeyCode();
        this.extendedKeyCode = event.getExtendedKeyCode();
        this.keyChar = event.getKeyChar();
        this.modifiers = event.getModifiersEx();
        this.keyLocation = event.getKeyLocation();
        this.when = event.getWhen();
    }

    /**
     * Returns the Main instance used in the event.
     *
     * @return The Main instance.
     */
    public BubbleBlaster getMain() {
        return main;
    }

    /**
     * Returns the KeyboardController instance used in the event.
     *
     * @return The KeyboardController instance.
     */
    public KeyboardInput getController() {
        return controller;
    }

    public KeyEvent getParentEvent() {
        return parentEvent;
    }

    public KeyEventType getType() {
        return type;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public int getExtendedKeyCode() {
        return extendedKeyCode;
    }

    public char getKeyChar() {
        return keyChar;
    }

    public int getModifiers() {
        return modifiers;
    }

    public int getKeyLocation() {
        return keyLocation;
    }

    public long getWhen() {
        return when;
    }

    public HashMap<Integer, Boolean> getPressed() {
        return pressed;
    }
}
