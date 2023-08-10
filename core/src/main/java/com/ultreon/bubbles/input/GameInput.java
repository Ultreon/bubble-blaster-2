package com.ultreon.bubbles.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v1.InputEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.gui.screen.PauseScreen;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.render.gui.screen.ScreenManager;
import com.ultreon.bubbles.vector.Vec2i;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.HashMap;
import java.util.Map;

public class GameInput extends InputAdapter {

    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private static final IntSet KEYS_DOWN = new IntArraySet();
    private static final Map<Integer, Boolean> buttonMap = new HashMap<>();
    private static final Int2ReferenceArrayMap<Vec2i> dragStarts = new Int2ReferenceArrayMap<>();
    private Vec2i pos;

    public static boolean isKeyDown(int keyCode) {
        return KEYS_DOWN.contains(keyCode);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (isKeyDown(keycode)) {
            InputEvents.KEY_PRESS.factory().onKeyPress(keycode, true);
        } else {
            BubbleBlaster.getInstance().keyPress(keycode);
            InputEvents.KEY_PRESS.factory().onKeyPress(keycode, false);
            KEYS_DOWN.add(keycode);
        }

        ScreenManager screenManager = this.game.getScreenManager();
        Screen screen = screenManager.getCurrentScreen();
        Environment environment = game.environment;

        if (screen != null)
            screen.keyPress(keycode);
        else if (keycode == Input.Keys.ESCAPE && environment != null && environment.isAlive())
            BubbleBlaster.getInstance().showScreen(new PauseScreen());

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        KEYS_DOWN.remove(keycode);
        InputEvents.KEY_RELEASE.factory().onKeyRelease(keycode);

        ScreenManager screenManager = this.game.getScreenManager();
        Screen screen = screenManager.getCurrentScreen();

        if (screen != null) screen.keyRelease(keycode);

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        InputEvents.CHAR_TYPE.factory().onCharType(character);

        ScreenManager screenManager = this.game.getScreenManager();
        Screen currentScreen = screenManager.getCurrentScreen();
        if (currentScreen != null) {
            currentScreen.charType(character);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        this.pos = new Vec2i(screenX, screenY);

        InputEvents.MOUSE_MOVE.factory().onMouseMove(screenX, screenY);

        ScreenManager screenManager = game.getScreenManager();
        Screen currentScreen = screenManager.getCurrentScreen();

        if (currentScreen != null) currentScreen.mouseMove(screenX, screenY);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.pos = new Vec2i(screenX, screenY);

        buttonMap.put(button, false);
        dragStarts.remove(button);

        InputEvents.MOUSE_RELEASE.factory().onMouseRelease(screenX, screenY, button);

        ScreenManager screenManager = game.getScreenManager();
        Screen currentScreen = screenManager.getCurrentScreen();

        if (currentScreen != null) currentScreen.mouseRelease(screenX, screenY, button);

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.pos = new Vec2i(screenX, screenY);

        buttonMap.put(button, true);
        dragStarts.put(button, new Vec2i(screenX, screenY));

        InputEvents.MOUSE_PRESS.factory().onMousePress(screenX, screenY, button);
        InputEvents.MOUSE_CLICK.factory().onMouseClick(screenX, screenY, button, 1);

        ScreenManager screenManager = game.getScreenManager();
        Screen currentScreen = screenManager.getCurrentScreen();

        if (currentScreen != null) currentScreen.mousePress(screenX, screenY, button);
        if (currentScreen != null) currentScreen.mouseClick(screenX, screenY, button, 1);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        this.pos = new Vec2i(screenX, screenY);

        InputEvents.MOUSE_DRAG.factory().onMouseDrag(screenX, screenY, Input.Buttons.LEFT);

        for (var entry : dragStarts.int2ReferenceEntrySet()) {
            ScreenManager screenManager = game.getScreenManager();
            Screen currentScreen = screenManager.getCurrentScreen();
            Vec2i vec = entry.getValue();

            if (currentScreen != null) currentScreen.mouseDrag(vec.x, vec.y, screenX, screenY, entry.getIntKey());
        }

        ScreenManager screenManager = game.getScreenManager();
        Screen currentScreen = screenManager.getCurrentScreen();

        if (currentScreen != null) currentScreen.mouseMove(screenX, screenY);

        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (this.pos == null) {
            return false;
        }
        InputEvents.MOUSE_SCROLL.factory().onMouseScroll(pos.x, pos.y, amountY);

        ScreenManager screenManager = game.getScreenManager();
        Screen currentScreen = screenManager.getCurrentScreen();
        if (currentScreen != null) {
            currentScreen.mouseWheel(pos.x, pos.y, amountY);
        }

        return true;
    }
}
