package com.ultreon.bubbles.render.screen;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.CrashButton;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.commons.crash.CrashLog;

import java.awt.*;
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
        renderer.color(new Color(192, 0, 0));
        renderer.rectLine(0, 0, game.getWidth(), game.getScaledHeight());

        GraphicsUtils.drawCenteredString(renderer, "The game crashed!", new Rectangle(20, 20, game.getScaledWidth() - 40, 30), new Font(game.getSansFontName(), Font.BOLD, 24));

        crashButton.setX(game.getScaledWidth() / 2 - crashButton.getBounds().width / 2);
    }
}
