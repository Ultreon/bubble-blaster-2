package com.ultreon.bubbles.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GamePlatform;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.commons.v0.Mth;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

public class ControllerHandler extends InputHandler<ControllerInput> {
    private static final float DEAD_ZONE = 0.2f;
    private static final float MOUSE_SPEED = 200f;
    private final BubbleBlaster game = BubbleBlaster.getInstance();

    public ControllerHandler() {
        super(ControllerInput.get(), InputType.Controller);
    }

    @Override
    public boolean tickScreen(Screen screen) {
        Controller controller = this.input.controller;

        if (controller == null || !controller.isConnected()) return false;

        ControllerMapping mapping = controller.getMapping();

        float moveX = controller.getAxis(mapping.axisLeftX);
        float moveY = controller.getAxis(mapping.axisLeftY);

        if (moveX > -DEAD_ZONE && moveX < DEAD_ZONE) moveX = 0f;
        if (moveY > -DEAD_ZONE && moveY < DEAD_ZONE) moveY = 0f;

        Vector2 cursorPos = KeyboardInput.getMousePos();
        if (moveX > 0 || moveY > 0) {
            cursorPos.add(moveX * MOUSE_SPEED / TPS, moveY * MOUSE_SPEED / TPS);
            cursorPos.x = Mth.clamp(cursorPos.x, 0, Gdx.graphics.getWidth());
            cursorPos.y = Mth.clamp(cursorPos.y, 0, Gdx.graphics.getHeight());

//            Vector2 screenCoordinates = this.game.viewport.toScreenCoordinates(cursorPos, this.game.getTransform());
//            Gdx.input.setCursorPosition((int) screenCoordinates.x, (int) screenCoordinates.y);
            Gdx.input.setCursorPosition((int) cursorPos.x, (int) cursorPos.y);
        }

        return true;
    }

    @Override
    public boolean tickWorld(World world, LoadedGame loadedGame) {
        return false;
    }

    @Override
    public boolean tickPlayer(Player player) {
        Controller controller = this.input.controller;

        if (controller == null || !controller.isConnected()) return false;

        ControllerMapping mapping = controller.getMapping();

        float moveX = controller.getAxis(mapping.axisLeftX);
        float moveY = controller.getAxis(mapping.axisLeftY);

        if (GamePlatform.get().isDesktop()) {
            float triggerL = controller.getAxis(4);
            float triggerR = controller.getAxis(5);
            if (triggerL < DEAD_ZONE) triggerL = 0;
            if (triggerR < DEAD_ZONE) triggerR = 0;
            if (triggerL > 0) {
                player.moving(triggerL);
            } else if (triggerR > 0.5f) {
                player.setBrake(true);
            } else if (triggerR <= 0.5f) {
                player.setBrake(false);
            }
        }

        if (moveX > -DEAD_ZONE && moveX < DEAD_ZONE) moveX = 0f;

        player.rotating(moveX);

        if (controller.getButton(mapping.buttonA)) player.shoot();
        if (controller.getButton(mapping.buttonB)) player.boost();

        return true;
    }
}
