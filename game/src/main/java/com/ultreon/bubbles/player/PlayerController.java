package com.ultreon.bubbles.player;

import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.core.input.KeyboardInput;
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
            this.controller.forward(KeyboardInput.isDown(KeyboardInput.Map.KEY_UP) || KeyboardInput.isDown(KeyboardInput.Map.KEY_KP_UP) || KeyboardInput.isDown(KeyboardInput.Map.KEY_W));
            this.controller.backward(KeyboardInput.isDown(KeyboardInput.Map.KEY_DOWN) || KeyboardInput.isDown(KeyboardInput.Map.KEY_KP_DOWN) || KeyboardInput.isDown(KeyboardInput.Map.KEY_S));
            this.controller.right(KeyboardInput.isDown(KeyboardInput.Map.KEY_RIGHT) || KeyboardInput.isDown(KeyboardInput.Map.KEY_KP_RIGHT) || KeyboardInput.isDown(KeyboardInput.Map.KEY_D));
            this.controller.left(KeyboardInput.isDown(KeyboardInput.Map.KEY_LEFT) || KeyboardInput.isDown(KeyboardInput.Map.KEY_KP_LEFT) || KeyboardInput.isDown(KeyboardInput.Map.KEY_A));
        }
    }
}
