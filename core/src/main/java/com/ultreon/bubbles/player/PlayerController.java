package com.ultreon.bubbles.player;

import com.badlogic.gdx.Input;
import com.ultreon.bubbles.input.GameInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerController {
    private final InputController controller;

    private static final Logger logger = LogManager.getLogger("Player-Controller");

    public PlayerController(InputController controller) {
        this.controller = controller;
    }

    public void tick() {
//        logger.info("PlayerController[8c724942]: " + this.player);
        if (this.controller != null) {
//            logger.info("PlayerController[8c217398]: " + this.player);
            this.controller.forward(GameInput.isKeyDown(Input.Keys.UP) || GameInput.isKeyDown(Input.Keys.W));
            this.controller.backward(GameInput.isKeyDown(Input.Keys.DOWN) || GameInput.isKeyDown(Input.Keys.S));
            this.controller.rotateRight(GameInput.isKeyDown(Input.Keys.RIGHT) || GameInput.isKeyDown(Input.Keys.D));
            this.controller.rotateLeft(GameInput.isKeyDown(Input.Keys.LEFT) || GameInput.isKeyDown(Input.Keys.A));
        }
    }
}
