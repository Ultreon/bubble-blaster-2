package com.ultreon.bubbles.event.v1.input;

import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.event.v1.Event;
import com.ultreon.bubbles.event.v1.type.MouseEventType;
import com.ultreon.bubbles.game.BubbleBlaster;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;

/**
 * Mouse Event
 * This event is used for handling mouse input.
 *
 * @see MouseMotionEvent
 * @see MouseWheelEvent
 * @see MouseInput
 * @see MouseEventType
 * @see java.awt.event.MouseEvent
 */
@Deprecated
public class MouseEvent extends Event {
    private final int button;
    private final Point locationOnScreen;
    private final int clickCount;
    private final Point point;
    private final int x;
    private final int xOnScreen;
    private final int y;
    private final int yOnScreen;

    private final BubbleBlaster game;
    private final @NonNull MouseInput controller;
    private final java.awt.event.MouseEvent parentEvent;
    private final MouseEventType type;

    /**
     * Keyboard event, called from a specific scene.
     *
     * @param game       The {@linkplain BubbleBlaster Bubble Blaster} instance.
     * @param controller The {@linkplain MouseInput mouse input} instance.
     * @param event      The {@linkplain java.awt.event.MouseEvent (awt) mouse event} instance.
     * @param type       One create the {@linkplain MouseEventType mouse event type} constants.
     */
    public MouseEvent(BubbleBlaster game, @NonNull MouseInput controller, java.awt.event.MouseEvent event, MouseEventType type) {
        this.game = game;
        this.type = type;
        this.button = event.getButton();
        this.clickCount = event.getClickCount();
        this.locationOnScreen = event.getLocationOnScreen();
        this.point = event.getPoint();
        this.x = event.getX();
        this.xOnScreen = event.getXOnScreen();
        this.y = event.getY();
        this.yOnScreen = event.getYOnScreen();
        this.controller = controller;
        this.parentEvent = event;
    }

    /**
     * Returns the Main instance used in the event.
     *
     * @return The Main instance.
     */
    public BubbleBlaster getGame() {
        return game;
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

    public int getButton() {
        return button;
    }

    public Point getLocationOnScreen() {
        return locationOnScreen;
    }

    public int getClickCount() {
        return clickCount;
    }

    public Point getPoint() {
        return point;
    }

    public int getX() {
        return x;
    }

    public int getXOnScreen() {
        return xOnScreen;
    }

    public int getY() {
        return y;
    }

    public int getYOnScreen() {
        return yOnScreen;
    }
}
