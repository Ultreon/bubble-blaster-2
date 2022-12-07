package com.ultreon.bubbles.event.v1.input;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.event.v1.Event;
import com.ultreon.bubbles.event.v1.type.KeyEventType;
import com.ultreon.bubbles.event.v1.type.MouseEventType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Mouse Motion Event
 * This event is used for handling mouse motion input.
 *
 * @see com.ultreon.bubbles.event.v1.input.MouseEvent
 * @see MouseMotionEvent
 * @see MouseInput
 * @see MouseEventType
 * @see java.awt.event.MouseEvent
 */
@Deprecated
public class MouseMotionEvent extends Event {
    private final BubbleBlaster main;
    private final @NonNull MouseInput controller;
    private final java.awt.event.MouseEvent parentEvent;
    private final MouseEventType type;

    /**
     * Keyboard event, called from a specific scene.
     *
     * @param main       The {@link BubbleBlaster} instance.
     * @param controller The {@link KeyboardInput} instance.
     * @param event      The {@link KeyEvent} instance.
     * @param type       One create the {@link KeyEventType} constants.
     */
    public MouseMotionEvent(BubbleBlaster main, @NonNull MouseInput controller, MouseEvent event, MouseEventType type) {
        this.main = main;
        this.type = type;
        this.controller = controller;
        this.parentEvent = event;
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
    public @NonNull MouseInput getController() {
        return controller;
    }

    public java.awt.event.MouseEvent getParentEvent() {
        return parentEvent;
    }

    public MouseEventType getType() {
        return type;
    }
}
