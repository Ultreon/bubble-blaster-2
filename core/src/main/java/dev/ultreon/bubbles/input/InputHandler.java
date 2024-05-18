package dev.ultreon.bubbles.input;

import dev.ultreon.bubbles.GamePlatform;
import dev.ultreon.bubbles.LoadedGame;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.screen.Screen;
import dev.ultreon.bubbles.world.World;

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
