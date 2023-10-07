package com.ultreon.bubbles.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.event.v1.InputEvents;
import com.ultreon.commons.exceptions.OneTimeUseException;
import com.ultreon.libs.datetime.v0.Duration;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@SuppressWarnings("NewApi")
public class MobileInput implements InputProcessor, InputObject {
    private static final Set<Integer> KEYS_DOWN = new CopyOnWriteArraySet<>();
    private static final GridPoint2 POS = new GridPoint2(Integer.MIN_VALUE, Integer.MIN_VALUE);
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final Map<Integer, Boolean> pressedByPointer = new HashMap<>();
    private final Int2ReferenceArrayMap<GridPoint2> dragStarts = new Int2ReferenceArrayMap<>();

    private static MobileInput instance;

    public MobileInput() {
        if (instance != null) throw new OneTimeUseException("Keyboard input can be created only once.");

        instance = this;
    }

    public static Vector2 getTouchPos() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    public static Vector2 getTouchPos(int pointer) {
        return new Vector2(Gdx.input.getX(pointer), Gdx.input.getY(pointer));
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
        var pressureAvailable = MobileInput.isPressureAvailable() && BubbleBlasterConfig.ENABLE_TOUCH_PRESSURE.get();
        return pressureAvailable ? Gdx.input.getPressure(0) : Gdx.input.isTouched(0) ? 1f : 0f;
    }

    public static boolean isPressureAvailable() {
        return Gdx.input.isPeripheralAvailable(Input.Peripheral.Pressure);
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

    @Deprecated
    public static GridPoint2 getMousePoint() {
        return new GridPoint2(POS.x, POS.y);
    }

    public static boolean isKeyDown(int keycode) {
        return KEYS_DOWN.contains(keycode);
    }

    public static boolean areKeysDown(int... keys) {
        return Arrays.stream(keys).allMatch(MobileInput::isKeyDown);
    }

    public static boolean isAnyKeyDownOf(int... keys) {
        return Arrays.stream(keys).anyMatch(MobileInput::isKeyDown);
    }

    public static MobileInput get() {
        return instance;
    }

    public static boolean isTouchDown() {
        return Gdx.input.isTouched();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (MobileInput.isKeyDown(keycode)) {
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
