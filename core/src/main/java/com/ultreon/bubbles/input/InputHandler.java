package com.ultreon.bubbles.input;

import com.ultreon.bubbles.GamePlatform;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.world.World;

import java.util.HashMap;
import java.util.Map;

public abstract class InputHandler<T extends InputObject> {
    private static final Map<InputType, InputHandler<?>> TYPES = new HashMap<>();
    protected static InputType currentInput = InputType.KeyboardAndMouse;
    protected final T input;
    private final InputType type;

    public InputHandler(T input, InputType type) {
        this.input = input;
        this.type = type;

        if (TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    public final void tickInputType() {
        var controller = ControllerInput.get().controller;
        if (controller == null || !controller.isConnected()) {
            InputHandler.touchscreenOrKeyboard();
            return;
        }

        currentInput = InputType.Controller;
    }

    private static void touchscreenOrKeyboard() {
        if (GamePlatform.get().isMobile()) {

        }
    }

    public abstract boolean tickScreen(Screen screen);

    public abstract boolean tickWorld(World world, LoadedGame loadedGame);

    public abstract boolean tickPlayer(Player player);

    public InputType getType() {
        return this.type;
    }

    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {

    }
}
