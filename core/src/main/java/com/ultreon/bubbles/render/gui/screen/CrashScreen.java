package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.font.Thickness;
import com.ultreon.bubbles.render.gui.widget.CrashButton;
import com.ultreon.commons.crash.CrashLog;

import java.io.File;

public class CrashScreen extends Screen {
    private final CrashLog report;
    private final CrashButton crashButton;
    private final String reportName;

    public CrashScreen(CrashLog crashLog) {
        super();
        this.report = crashLog;
        this.reportName = crashLog.getDefaultFileName();

        BubbleBlaster bb = BubbleBlaster.getInstance();
        this.crashButton = this.add(new CrashButton(bb.getScaledWidth() / 2 - 64, 60, 128, 24));
        this.crashButton.setText("Open crash report \uD83D\uDCCE");
    }

    @Override
    public void init() {
        this.report.writeToFile(new File(reportName));
    }

    @Override
    public boolean onClose(Screen to) {
        return false;
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        renderer.setColor(Color.rgb(0xc00000));
        renderer.rectLine(0, 0, game.getWidth(), game.getScaledHeight());

        font.draw(renderer, "The game crashed!", 24, width / 2f, 25, Thickness.BOLD, Anchor.CENTER);

        crashButton.setX(game.getScaledWidth() / 2 - crashButton.getBounds().width / 2);
    }
}
