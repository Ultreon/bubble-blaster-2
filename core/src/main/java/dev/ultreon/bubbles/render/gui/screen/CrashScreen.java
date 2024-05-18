package dev.ultreon.bubbles.render.gui.screen;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.init.Fonts;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Insets;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.widget.CrashButton;
import dev.ultreon.libs.crash.v0.CrashLog;
import dev.ultreon.libs.text.v1.TextObject;

public class CrashScreen extends Screen {
    private final CrashLog crashLog;
    private final String fileName;
    private CrashButton crashButton;

    public CrashScreen(CrashLog crashLog) {
        super(TextObject.translation("bubbles/screen/crash/title"));
        this.crashLog = crashLog;
        this.fileName = crashLog.getDefaultFileName();

        this.crashLog.writeToFile(BubbleBlaster.getDataDir().child(this.fileName).file());
    }

    @Override
    public void init() {
        this.clearWidgets();

        this.crashButton = this.add(new CrashButton(this.game.getScaledWidth() / 2 - 64, 60, 128, 24));
        this.crashButton.setText("Open crash report \uD83D\uDCCE");
    }

    @Override
    public boolean close(Screen to) {
        return true;
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        renderer.box(0, 0, this.width, game.getScaledHeight(), Color.CRIMSON, new Insets(10));

        renderer.drawTextCenter(Fonts.DONGLE_PAUSE.get(), "The game crashed!", this.width / 2f, 25, Color.CRIMSON);

        this.crashButton.setX((int) (game.getScaledWidth() / 2 - this.crashButton.getBounds().width / 2));
    }

    public CrashLog getCrashLog() {
        return this.crashLog;
    }

    public String getFileName() {
        return this.fileName;
    }
}
