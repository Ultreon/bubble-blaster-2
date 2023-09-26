package com.ultreon.bubbles.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.GridPoint2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v1.InputEvents;
import com.ultreon.bubbles.render.gui.screen.PauseScreen;
import com.ultreon.bubbles.render.gui.screen.Screen;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class GameInput implements InputProcessor {
    private static final Set<Integer> KEYS_DOWN = new CopyOnWriteArraySet<>();
    private static final GridPoint2 POS = new GridPoint2(Integer.MIN_VALUE, Integer.MIN_VALUE);
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final Map<Integer, Boolean> pressedByPointer = new HashMap<>();
    private final Int2ReferenceArrayMap<GridPoint2> dragStarts = new Int2ReferenceArrayMap<>();

    private static final int SHIFT_MASK = 1;
    private static final int CTRL_MASK = 1 << 1;
    private static final int META_MASK = 1 << 2;
    private static final int ALT_MASK = 1 << 3;
    private static final int ALT_GRAPH_MASK = 1 << 5;

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

    public static GridPoint2 getPos() {
        return POS.cpy();
    }

    public static boolean isKeyDown(int keycode) {
        return KEYS_DOWN.contains(keycode);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (isKeyDown(keycode)) {
            InputEvents.KEY_PRESS.factory().onKeyPress(keycode, true);
            return true;
        }

        KEYS_DOWN.add(keycode);
        InputEvents.KEY_PRESS.factory().onKeyPress(keycode, false);

        return this.game.keyPress(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        KEYS_DOWN.remove(keycode);
        InputEvents.KEY_RELEASE.factory().onKeyRelease(keycode);

        return this.game.keyRelease(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        InputEvents.CHAR_TYPE.factory().onCharType(character);

        return this.game.charType(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0) POS.set(screenX, screenY);
        pressedByPointer.put(pointer, true);

        dragStarts.put(button, new GridPoint2(screenX, screenY));

        InputEvents.MOUSE_PRESS.factory().onMousePress(screenX, screenY, button);

        return this.game.mousePress(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0) POS.set(screenX, screenY);

        pressedByPointer.put(pointer, false);

        dragStarts.remove(button);

        InputEvents.MOUSE_RELEASE.factory().onMouseRelease(screenX, screenY, button);

        return this.game.mouseRelease(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer == 0) POS.set(screenX, screenY);

        for (var entry : dragStarts.int2ReferenceEntrySet()) {
            GridPoint2 vec = entry.getValue();
            this.game.mouseDragged(vec.x, vec.y, screenX, screenY, pointer, entry.getIntKey());
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        POS.set(screenX, screenY);

        InputEvents.MOUSE_MOVE.factory().onMouseMove(screenX, screenY);

        return this.game.mouseMove(screenX, screenY);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        InputEvents.MOUSE_SCROLL.factory().onMouseScroll(POS.x, POS.y, amountY);

        return this.game.mouseWheel(POS.x, POS.y, amountX, amountY);
    }
}
