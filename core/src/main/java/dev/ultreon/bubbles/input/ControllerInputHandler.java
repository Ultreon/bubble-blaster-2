package dev.ultreon.bubbles.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.GamePlatform;
import dev.ultreon.bubbles.LoadedGame;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.render.gui.screen.Screen;
import dev.ultreon.bubbles.shop.ShopScreen;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.libs.commons.v0.Mth;

import static dev.ultreon.bubbles.BubbleBlaster.TPS;

public class ControllerInputHandler extends InputHandler<ControllerInput> {
    private static final float DEAD_ZONE = 0.2f;
    private static final float MOUSE_SPEED = 200f;
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private boolean backPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean middlePressed;

    public ControllerInputHandler() {
        super(ControllerInput.get(), InputType.Controller);
    }

    @Override
    public boolean tickScreen(Screen screen) {
        var controller = this.input.controller;

        if (controller == null || !controller.isConnected()) return false;

        var mapping = controller.getMapping();

        var moveX = controller.getAxis(mapping.axisLeftX) * 2;
        var moveY = controller.getAxis(mapping.axisLeftY) * 2;

        var scroll = controller.getAxis(mapping.axisRightY);

        if (moveX > -DEAD_ZONE && moveX < DEAD_ZONE) moveX = 0f;
        if (moveY > -DEAD_ZONE && moveY < DEAD_ZONE) moveY = 0f;
        if (scroll > -DEAD_ZONE && scroll < DEAD_ZONE) scroll = 0f;

        var cursorPos = DesktopInput.getMousePos();
        if (moveX != 0 || moveY != 0) {
            cursorPos.add(moveX * MOUSE_SPEED / TPS, moveY * MOUSE_SPEED / TPS);
            cursorPos.x = Mth.clamp(cursorPos.x, 0, Gdx.graphics.getWidth() - 1);
            cursorPos.y = Mth.clamp(cursorPos.y, 0, Gdx.graphics.getHeight() - 1);

//            Vector2 screenCoordinates = this.game.viewport.toScreenCoordinates(cursorPos, this.game.getTransform());
//            Gdx.input.setCursorPosition((int) screenCoordinates.x, (int) screenCoordinates.y);
            Gdx.input.setCursorPosition((int) cursorPos.x, (int) cursorPos.y);
        }
        screen.mouseWheel((int) cursorPos.x, (int) cursorPos.y, scroll);

        if (controller.getButton(mapping.buttonL1) || controller.getButton(mapping.buttonA)) {
            this.leftPressed = true;
            screen.mousePress((int) cursorPos.x, (int) cursorPos.y, Input.Buttons.LEFT);
        } else if (this.leftPressed) {
            this.leftPressed = false;
            screen.mouseRelease((int) cursorPos.x, (int) cursorPos.y, Input.Buttons.LEFT);
        }
        if (controller.getButton(mapping.buttonR1)) {
            this.rightPressed = true;
            screen.mousePress((int) cursorPos.x, (int) cursorPos.y, Input.Buttons.RIGHT);
        } else if (this.rightPressed) {
            this.rightPressed = false;
            screen.mouseRelease((int) cursorPos.x, (int) cursorPos.y, Input.Buttons.RIGHT);
        }
        if (controller.getButton(mapping.buttonStart)) {
            this.middlePressed = true;
            screen.mousePress((int) cursorPos.x, (int) cursorPos.y, Input.Buttons.MIDDLE);
        } else if (this.middlePressed) {
            this.middlePressed = false;
            screen.mouseRelease((int) cursorPos.x, (int) cursorPos.y, Input.Buttons.MIDDLE);
        }
        if (controller.getButton(mapping.buttonB)) {
            this.backPressed = true;
            screen.keyPress(Input.Keys.BACK);
        } else if (this.backPressed) {
            this.backPressed = false;
            screen.keyRelease(Input.Keys.BACK);
        }

        return true;
    }

    @Override
    public boolean tickWorld(World world, LoadedGame loadedGame) {
        return false;
    }

    @Override
    public boolean tickPlayer(Player player) {
        var controller = this.input.controller;

        if (controller == null || !controller.isConnected()) return false;

        var mapping = controller.getMapping();

        var moveX = controller.getAxis(mapping.axisLeftX);
        var moveY = controller.getAxis(mapping.axisLeftY);

        var triggerL = controller.getAxis(4);
        var triggerR = controller.getAxis(5);
        if (triggerL < DEAD_ZONE) triggerL = 0;
        if (triggerR < DEAD_ZONE) triggerR = 0;
        if (triggerR > 0.5f) {
            player.setBrake(true);
        } else if (triggerR <= 0.5f) {
            player.setBrake(false);
        } else if (triggerL > 0) {
            player.moving(triggerL);
        }

        if (moveX > -DEAD_ZONE && moveX < DEAD_ZONE) moveX = 0f;

        player.rotating(moveX);

        if (controller.getButton(mapping.buttonA)) player.shoot();
        if (controller.getButton(mapping.buttonB)) player.boost();
        if (controller.getButton(mapping.buttonY)) this.game.showScreen(new ShopScreen());

        return true;
    }
}
