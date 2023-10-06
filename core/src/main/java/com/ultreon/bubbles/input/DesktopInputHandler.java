package com.ultreon.bubbles.input;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GamePlatform;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.render.gui.screen.CommandScreen;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.world.World;

public class DesktopInputHandler extends InputHandler<DesktopInput> {
    public DesktopInputHandler() {
        super(DesktopInput.get(), InputType.KeyboardAndMouse);
    }

    @Override
    public boolean tickScreen(Screen screen) {
        return true;
    }

    @Override
    public boolean tickWorld(World world, LoadedGame loadedGame) {
        if (!GamePlatform.get().isDesktop()) return false;

        if (KeyBindings.COMMAND.isJustPressed()) {
            BubbleBlaster.getInstance().showScreen(new CommandScreen());
            return true;
        }
        return false;
    }

    @Override
    public boolean tickPlayer(Player player) {
        if (!GamePlatform.get().isDesktop()) return false;

        float rotating = 0f;
        float moving = 0f;

        if (KeyBindings.FORWARD.isPressed()) moving += 1f;
        if (KeyBindings.ROTATE_LEFT.isPressed()) rotating -= 1f;
        if (KeyBindings.ROTATE_RIGHT.isPressed()) rotating += 1f;

        player.moving(moving);
        player.rotating(rotating);

        if (KeyBindings.SHOOT.isJustPressed()) player.shoot();
        if (KeyBindings.BOOST.isJustPressed()) player.boost();
        player.setBrake(KeyBindings.BRAKE.isPressed());
        return false;
    }
}
