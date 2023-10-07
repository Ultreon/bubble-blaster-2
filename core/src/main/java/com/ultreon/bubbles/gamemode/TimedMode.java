package com.ultreon.bubbles.gamemode;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.gameplay.GameplayStorage;
import com.ultreon.bubbles.init.HudTypes;
import com.ultreon.bubbles.render.gui.hud.HudType;
import com.ultreon.bubbles.world.World;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.text.v1.TextObject;
import org.jetbrains.annotations.NotNull;

public class TimedMode extends NormalMode {
    private static final TextObject TIMED_OUT = TextObject.translation("bubbleblaster.screen.gameOver.timedOut");

    private boolean outOfTime = false;
    private World world;
    private GameplayStorage gameplayStorage;
    private long createTime;

    public TimedMode() {

    }

    @Override
    public @NotNull HudType getHudOverride() {
        return HudTypes.TIMED.get();
    }

    @Override
    public void begin() {
        super.begin();

        var world = this.game.world;
        if (world == null) throw new IllegalStateException("The world isn't loaded in.");
        this.world = world;

        this.gameplayStorage = this.world.getGameplayStorage();
        this.load(this.gameplayStorage.get(BubbleBlaster.NAMESPACE));
        this.gameplayStorage.set(BubbleBlaster.NAMESPACE, this.save());
    }

    @Override
    public void tick(@NotNull World world) {
        if (this.getTimeRemaining() <= 0 && !this.outOfTime) {
            this.outOfTime = true;
            world.triggerGameOver(TIMED_OUT);
        }
    }

    private void load(MapType data) {
        this.createTime = data.getLong("createTime", System.currentTimeMillis());
        this.outOfTime = data.getBoolean("outOfTime", false);
    }

    private MapType save() {
        var data = new MapType();
        data.putLong("createTime", this.createTime);
        return data;
    }

    @Override
    public int getBulletPops() {
        return 4;
    }

    @Override
    public boolean canBePaused() {
        return false;
    }

    public long getTimeRemaining() {
        return this.getEndTime() - System.currentTimeMillis();
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public long getEndTime() {
        return this.createTime + BubbleBlasterConfig.TIME_LIMIT.get() * 1000;
    }

    @Override
    public void end() {
        super.end();

        this.gameplayStorage = null;
        this.createTime = 0L;
        this.outOfTime = false;
        this.world = null;
    }
}
