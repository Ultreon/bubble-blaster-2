package dev.ultreon.bubbles.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.event.v1.InputEvents;
import dev.ultreon.bubbles.util.exceptions.OneTimeUseException;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class DesktopInput implements InputProcessor, InputObject {
    private static final Set<Integer> KEYS_DOWN = new CopyOnWriteArraySet<>();
    private static final GridPoint2 POS = new GridPoint2(Integer.MIN_VALUE, Integer.MIN_VALUE);
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final Map<Integer, Boolean> pressedByPointer = new HashMap<>();
    private final Int2ReferenceArrayMap<GridPoint2> dragStarts = new Int2ReferenceArrayMap<>();

    private static DesktopInput instance;

    public DesktopInput() {
        if (instance != null) throw new OneTimeUseException("Keyboard input can be created only once.");

        instance = this;
    }

    public static boolean isCtrlDown() {
        return DesktopInput.isAnyKeyDownOf(Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT);
    }

    public static boolean isAltDown() {
        return DesktopInput.isAnyKeyDownOf(Keys.ALT_LEFT, Keys.ALT_RIGHT);
    }

    public static boolean isShiftDown() {
        return DesktopInput.isAnyKeyDownOf(Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT);
    }

    public static Vector2 getMousePos() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    public static Vector2 getMouseDelta() {
        return new Vector2(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
    }

    @Deprecated
    public static GridPoint2 getMousePoint() {
        return new GridPoint2(POS.x, POS.y);
    }

    public static boolean isKeyDown(int keycode) {
        return KEYS_DOWN.contains(keycode);
    }

    public static boolean areKeysDown(int... keys) {
        return Arrays.stream(keys).allMatch(DesktopInput::isKeyDown);
    }

    public static boolean isAnyKeyDownOf(int... keys) {
        return Arrays.stream(keys).anyMatch(DesktopInput::isKeyDown);
    }

    public static DesktopInput get() {
        return instance;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (DesktopInput.isKeyDown(keycode)) {
            InputEvents.KEY_PRESS.factory().onKeyPress(keycode, true);
            return true;
        }

        KEYS_DOWN.add(keycode);
        InputEvents.KEY_PRESS.factory().onKeyPress(keycode, false);

        this.game.keyPress(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        KEYS_DOWN.remove(keycode);
        InputEvents.KEY_RELEASE.factory().onKeyRelease(keycode);

        this.game.keyRelease(keycode);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        InputEvents.CHAR_TYPE.factory().onCharType(character);

        this.game.charType(character);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0) POS.set(screenX, screenY);
        this.pressedByPointer.put(pointer, true);

        this.dragStarts.put(button, new GridPoint2(screenX, screenY));

        InputEvents.MOUSE_PRESS.factory().onMousePress(screenX, screenY, button);

        return this.game.mousePress(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == 0) POS.set(screenX, screenY);

        this.pressedByPointer.put(pointer, false);

        this.dragStarts.remove(button);

        InputEvents.MOUSE_RELEASE.factory().onMouseRelease(screenX, screenY, button);

        return this.game.mouseRelease(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer == 0) POS.set(screenX, screenY);

        for (var entry : this.dragStarts.int2ReferenceEntrySet()) {
            var vec = entry.getValue();
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