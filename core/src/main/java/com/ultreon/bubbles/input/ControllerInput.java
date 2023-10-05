package com.ultreon.bubbles.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.notification.Notification;

public class ControllerInput extends ControllerAdapter implements InputObject {
    private static ControllerInput instance;
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    protected Controller controller = Controllers.getCurrent();

    public ControllerInput() {
        if (instance != null) return;
        instance = this;
    }

    public static ControllerInput get() {
        return instance;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if (this.controller != controller) return super.buttonDown(controller, buttonIndex);

        ControllerMapping mapping = controller.getMapping();

        Vector2 cursorPos = KeyboardInput.getMousePos();
        if (buttonIndex == mapping.buttonL1) this.game.mousePress((int) cursorPos.x, (int) cursorPos.y, 0, Input.Buttons.LEFT);
        if (buttonIndex == mapping.buttonL2) this.game.mousePress((int) cursorPos.x, (int) cursorPos.y, 0, Input.Buttons.RIGHT);

        if (this.game.isInGame()) {
            if (this.game.hasScreenOpen() && mapping.buttonStart == buttonIndex)
                this.game.resume();
            if (!this.game.hasScreenOpen() && mapping.buttonStart == buttonIndex)
                this.game.pause();
        }

        return super.buttonDown(controller, buttonIndex);
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        if (this.controller != controller) return super.buttonUp(controller, buttonIndex);

        ControllerMapping mapping = controller.getMapping();

        Vector2 cursorPos = KeyboardInput.getMousePos();
        if (buttonIndex == mapping.buttonL1) this.game.mouseRelease((int) cursorPos.x, (int) cursorPos.y, 0, Input.Buttons.LEFT);
        if (buttonIndex == mapping.buttonL2) this.game.mouseRelease((int) cursorPos.x, (int) cursorPos.y, 0, Input.Buttons.RIGHT);

        return super.buttonUp(controller, buttonIndex);
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        return super.axisMoved(controller, axisIndex, value);
    }

    @Override
    public void connected(Controller controller) {
        String name = controller.getName();
        this.game.notifications.notify(Notification.builder("Controller Connected", name).subText("Controller Input Manager").build());
        if (this.controller == null) {
            this.controller = controller;
        }
    }

    @Override
    public void disconnected(Controller controller) {
        String name = controller.getName();
        this.game.notifications.notify(Notification.builder("Controller Disconnected", name).subText("Controller Input Manager").build());
        if (this.controller == controller) {
            this.controller = null;
        }
    }
}
