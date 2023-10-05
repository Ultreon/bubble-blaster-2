package com.ultreon.bubbles.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.event.v1.InputEvents;
import com.ultreon.commons.exceptions.OneTimeUseException;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class KeyboardInput implements InputProcessor, InputObject {
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
    private static KeyboardInput instance;

    public KeyboardInput() {
        if (instance != null) throw new OneTimeUseException("Keyboard input can be created only once.");

        instance = this;
    }

    public static boolean isShiftDown() {
        return KeyboardInput.isAnyKeyDownOf(Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT);
    }

    public static boolean isCtrlDown() {
        return KeyboardInput.isAnyKeyDownOf(Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT);
    }

    @Deprecated
    public static boolean isMetaDown() {
        return false;
    }

    public static boolean isAltDown() {
        return KeyboardInput.isAnyKeyDownOf(Keys.ALT_LEFT, Keys.ALT_RIGHT);
    }

    @Deprecated
    public static boolean isAltGraphDown() {
        return KeyboardInput.isCtrlDown() && KeyboardInput.isAltDown();
    }

    public static Vector2 getMousePos() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    public static Vector2 getMouseDelta() {
        return new Vector2(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
    }

    public static Vector3 getGyroscope() {
        return new Vector3(Gdx.input.getGyroscopeX(), Gdx.input.getGyroscopeY(), Gdx.input.getGyroscopeZ());
    }

    public static Vector3 getAccelerometer() {
        return new Vector3(Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());
    }

    public static float getTouchPressure() {
        return Gdx.input.getPressure();
    }

    public static float getPitch() {
        return Gdx.input.getPitch();
    }

    public static float getRoll() {
        return Gdx.input.getRoll();
    }

    public static void vibrate(Duration duration) {
        Gdx.input.vibrate((int) duration.toMillis());
    }

    public static void vibrate(Duration duration, boolean fallback) {
        Gdx.input.vibrate((int) duration.toMillis(), fallback);
    }

    public static void vibrate(Duration duration, int amplitude, boolean fallback) {
        Gdx.input.vibrate((int) duration.toMillis(), amplitude, fallback);
    }

    public static GridPoint2 getMousePoint() {
        return new GridPoint2(POS.x, POS.y);
    }

    public static boolean isKeyDown(int keycode) {
        return KEYS_DOWN.contains(keycode);
    }

    public static boolean areKeysDown(int... keys) {
        return Arrays.stream(keys).allMatch(KeyboardInput::isKeyDown);
    }

    public static boolean isAnyKeyDownOf(int... keys) {
        return Arrays.stream(keys).anyMatch(KeyboardInput::isKeyDown);
    }

    public static KeyboardInput get() {
        return instance;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (KeyboardInput.isKeyDown(keycode)) {
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
