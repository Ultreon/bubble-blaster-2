package com.ultreon.bubbles.core.input;

import com.ultreon.bubbles.event.v2.InputEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.screen.Screen;
import com.ultreon.bubbles.render.screen.ScreenManager;
import com.ultreon.bubbles.vector.Vec2i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Mouse controller for the Hydro Game Engine.
 * Should only for internal use.
 *
 * @author Qboi
 * @see KeyboardInput
 * @see java.awt.event.MouseAdapter
 */
@SuppressWarnings("ConstantConditions")
public abstract class MouseInput extends MouseAdapter {
    // Mouse input values.
    private Point currentLocationOnScreen;
    private Point currentPoint;
    private int clickCount;

    // Logger
    private static final Logger logger = LogManager.getLogger("Game-Input");

    // Other fields.
    private final Map<Integer, Boolean> buttonMap = new HashMap<>();
    private final BubbleBlaster game;
    private HashMap<Integer, Vec2i> dragStarts = new HashMap<>();

    /**
     *
     */
    public MouseInput() {
        this.game = BubbleBlaster.getInstance();
    }

    @Override
    public final void mouseClicked(MouseEvent e) {
        currentLocationOnScreen = e.getLocationOnScreen() != null ? e.getLocationOnScreen() : currentLocationOnScreen;
        currentPoint = e.getPoint() != null ? e.getPoint() : currentPoint;

        this.clickCount = e.getClickCount();

        InputEvents.MOUSE_CLICK.factory().onMouseClick(e.getX(), e.getY(), e.getButton(), e.getClickCount());

        ScreenManager screenManager = game.getScreenManager();
        if (screenManager != null) {
            Screen currentScreen = screenManager.getCurrentScreen();
            if (currentScreen != null) {
                currentScreen.mouseClick(e.getX(), e.getY(), e.getButton(), e.getClickCount());
            }
        }
    }

    @Override
    public final void mousePressed(MouseEvent e) {
        currentLocationOnScreen = e.getLocationOnScreen() != null ? e.getLocationOnScreen() : currentLocationOnScreen;
        currentPoint = e.getPoint() != null ? e.getPoint() : currentPoint;

        buttonMap.put(e.getButton(), true);

        dragStarts.put(e.getButton(), new Vec2i(e.getPoint()));

        InputEvents.MOUSE_PRESS.factory().onMousePress(e.getX(), e.getY(), e.getButton());

        ScreenManager screenManager = game.getScreenManager();
        if (screenManager != null) {
            Screen currentScreen = screenManager.getCurrentScreen();
            if (currentScreen != null) {
                currentScreen.mousePress(e.getX(), e.getY(), e.getButton());
            }
        }
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
        currentLocationOnScreen = e.getLocationOnScreen() != null ? e.getLocationOnScreen() : currentLocationOnScreen;
        currentPoint = e.getPoint() != null ? e.getPoint() : currentPoint;

        buttonMap.put(e.getButton(), false);

        dragStarts.remove(e.getButton());

        InputEvents.MOUSE_RELEASE.factory().onMouseRelease(e.getX(), e.getY(), e.getButton());

        ScreenManager screenManager = game.getScreenManager();
        if (screenManager != null) {
            Screen currentScreen = screenManager.getCurrentScreen();
            if (currentScreen != null) {
                currentScreen.mouseRelease(e.getX(), e.getY(), e.getButton());
            }
        }
    }

    @Override
    public final void mouseEntered(MouseEvent e) {
        currentLocationOnScreen = e.getLocationOnScreen() != null ? e.getLocationOnScreen() : currentLocationOnScreen;
        currentPoint = e.getPoint() != null ? e.getPoint() : currentPoint;

        InputEvents.MOUSE_ENTER_WINDOW.factory().onMouseEnterWindow(e.getX(), e.getY());
    }

    @Override
    public final void mouseExited(MouseEvent e) {
        currentLocationOnScreen = e.getLocationOnScreen() != null ? e.getLocationOnScreen() : currentLocationOnScreen;
        currentPoint = e.getPoint() != null ? e.getPoint() : currentPoint;

        InputEvents.MOUSE_EXIT_WINDOW.factory().onMouseExitWindow();

        ScreenManager screenManager = game.getScreenManager();
        if (screenManager != null) {
            Screen currentScreen = screenManager.getCurrentScreen();
            if (currentScreen != null) {
                currentScreen.mouseExit();
            }
        }
    }

    @Override
    public final void mouseDragged(MouseEvent e) {
        currentLocationOnScreen = e.getLocationOnScreen() != null ? e.getLocationOnScreen() : currentLocationOnScreen;
        currentPoint = e.getPoint() != null ? e.getPoint() : currentPoint;

        InputEvents.MOUSE_DRAG.factory().onMouseDrag(e.getX(), e.getY(), e.getButton());
    }

    @Override
    public final void mouseMoved(MouseEvent e) {
        currentLocationOnScreen = e.getLocationOnScreen() != null ? e.getLocationOnScreen() : currentLocationOnScreen;
        currentPoint = e.getPoint() != null ? e.getPoint() : currentPoint;

        for (Map.Entry<Integer, Vec2i> entry : dragStarts.entrySet()) {
            ScreenManager screenManager = game.getScreenManager();
            if (screenManager != null) {
                Screen currentScreen = screenManager.getCurrentScreen();
                if (currentScreen != null) {
                    Vec2i vec = entry.getValue();
                    currentScreen.mouseDrag(e.getX(), e.getY(), vec.x, vec.y, entry.getKey());
                }
            }
        }

        InputEvents.MOUSE_MOVE.factory().onMouseMove(e.getX(), e.getY());

        ScreenManager screenManager = game.getScreenManager();
        if (screenManager != null) {
            Screen currentScreen = screenManager.getCurrentScreen();
            if (currentScreen != null) {
                currentScreen.mouseMove(e.getX(), e.getY());
            }
        }
    }

    @Override
    public final void mouseWheelMoved(MouseWheelEvent e) {
        currentLocationOnScreen = e.getLocationOnScreen() != null ? e.getLocationOnScreen() : currentLocationOnScreen;
        currentPoint = e.getPoint() != null ? e.getPoint() : currentPoint;

        InputEvents.MOUSE_SCROLL.factory().onMouseScroll(e.getX(), e.getY(), e.getPreciseWheelRotation());

        ScreenManager screenManager = game.getScreenManager();
        if (screenManager != null) {
            Screen currentScreen = screenManager.getCurrentScreen();
            if (currentScreen != null) {
                currentScreen.mouseWheel(e.getX(), e.getY(), e.getPreciseWheelRotation(), e.getScrollAmount(), e.getUnitsToScroll());
            }
        }

    }

    protected Vec2i getCurrentLocationOnScreen() {
        return new Vec2i(currentLocationOnScreen);
    }

    protected Vec2i getCurrentPoint() {
        Point pos = currentPoint;
        if (pos == null) {
            return new Vec2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        return new Vec2i(pos);
//        return BubbleBlaster.getInstance().getGameWindow().getMousePosition();
    }

    protected int getClickCount() {
        return clickCount;
    }

    protected boolean isPressed(int button) {
        return buttonMap.getOrDefault(button, false);
    }
}
