package com.ultreon.bubbles.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Shape2D;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GamePlatform;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.commons.v0.Mth;

public class MobileInputHandler extends InputHandler<MobileInput> {
    public boolean shoot = false;
    public boolean boost = false;
    public boolean brake = false;

    public MobileInputHandler() {
        super(MobileInput.get(), InputType.TouchScreen);
    }

    @Override
    public boolean tickScreen(Screen screen) {
        return true;
    }

    @Override
    public boolean tickWorld(World world, LoadedGame loadedGame) {
        if (!GamePlatform.get().isMobile())
            return false;

        BubbleBlaster game = BubbleBlaster.getInstance();
        if (MobileInput.isTouchDown() && this.getPauseBtnRegion().contains(MobileInput.getTouchPos()))
            game.pause();

        return true;
    }

    @Override
    public boolean tickPlayer(Player player) {
        if (!GamePlatform.get().isMobile()) return false;

        float touchPressure = MobileInput.getTouchPressure();
        if (touchPressure < 0.2) touchPressure = 0;
        player.moving(Mth.clamp(touchPressure, 0, 1));
        player.setRotation(player.getAngleTo(MobileInput.getTouchPos()));

        this.shoot = this.getShootBtnRegion().contains(MobileInput.getTouchPos(1));

        if (this.shoot) {
            this.shoot = false;
            player.shoot();
        }
        if (this.boost) {
            this.boost = false;
            player.boost();
        }
        player.setBrake(this.brake);
        return false;
    }

    public Shape2D getShootBtnRegion() {
        return new Circle(Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 100, 100);
    }

    public Shape2D getPauseBtnRegion() {
        return new Circle(Gdx.graphics.getWidth() / 2f, 100, 100);
    }
}
