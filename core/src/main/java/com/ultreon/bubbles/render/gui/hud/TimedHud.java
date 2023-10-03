package com.ultreon.bubbles.render.gui.hud;

import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.gamemode.TimedMode;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.util.TimeUtils;

import java.time.Duration;

public class TimedHud extends ModernHud {
    @Override
    public void renderHudOverlay(Renderer renderer, World world, Gamemode gamemode, float deltaTime) {
        super.renderHudOverlay(renderer, world, gamemode, deltaTime);

        if (!(gamemode instanceof TimedMode timedGamemode)) return;

        Duration duration = Duration.ofMillis(timedGamemode.getTimeRemaining());
        int x = 20;
        int y = 120;

        long seconds = duration.toSeconds();
        if (!duration.isNegative()) {
            renderer.fill(x, y, 300, 80, Color.BLACK.withAlpha(0x80));
            renderer.drawTextCenter(Fonts.SANS_BOLD_60.get(), TimeUtils.formatDuration(duration), x + 150, y + 31, seconds <= 5 ? Color.CRIMSON : Color.WHITE);
        }
    }
}
