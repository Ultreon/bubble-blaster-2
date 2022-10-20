package com.ultreon.bubbles.player;

import com.ultreon.bubbles.input.KeyInput;
import com.ultreon.bubbles.input.KeyInput.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("ClassCanBeRecord")
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
            this.controller.forward(KeyInput.isDown(Map.KEY_UP) || KeyInput.isDown(Map.KEY_KP_UP) || KeyInput.isDown(Map.KEY_W));
            this.controller.backward(KeyInput.isDown(Map.KEY_DOWN) || KeyInput.isDown(Map.KEY_KP_DOWN) || KeyInput.isDown(Map.KEY_S));
            this.controller.right(KeyInput.isDown(Map.KEY_RIGHT) || KeyInput.isDown(Map.KEY_KP_RIGHT) || KeyInput.isDown(Map.KEY_D));
            this.controller.left(KeyInput.isDown(Map.KEY_LEFT) || KeyInput.isDown(Map.KEY_KP_LEFT) || KeyInput.isDown(Map.KEY_A));
        }
    }
}
