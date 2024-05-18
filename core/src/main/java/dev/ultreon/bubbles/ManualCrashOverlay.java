package dev.ultreon.bubbles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import dev.ultreon.bubbles.init.Fonts;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderable;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.libs.crash.v0.CrashCategory;
import dev.ultreon.libs.crash.v0.CrashLog;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class ManualCrashOverlay implements Renderable {
    private static final String BSOD = "\n:(\nBubble Blaster ran into a problem and needs to restart.\nStop code: MANUALLY_INITIATED_CRASH\nFile: " + ManualCrashOverlay.class.getSimpleName() + ".class";
    private boolean crashing = false;
    private Instant timer;

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        float width = renderer.getWidth();
        float height = renderer.getHeight();

        if (ManualCrashOverlay.isHoldingCrashKey() && BubbleBlaster.getInstance().isLoaded()) {
            if (!this.crashing) {
                this.crashing = true;
                this.setTimer();
            }

            var secondsLeft = this.timer.getEpochSecond() - Instant.now().getEpochSecond();

            renderer.fill(0, 0, width, height, Color.GRAY_4);
            renderer.fill(0, 0, (int) width, 10, Color.CRIMSON);
            renderer.fill(0, (int) (height - 10), (int) width, 10, Color.CRIMSON);

            renderer.drawText(Fonts.DONGLE_TITLE.get(), "Manually Initiating Crash", 50, 60, Color.CRIMSON);
            renderer.drawText(Fonts.MONOSPACED_HEADING_2.get(), "You have activated the MIC sequence!", 50, 210, Color.WHITE);
            renderer.drawText(Fonts.MONOSPACED_HEADING_2.get(), "If you didn't meant to activate this, stop holding any CTRL/Shift/Alt keys.", 50, 240, Color.WHITE);
            renderer.drawText(Fonts.MONOSPACED_HEADING_2.get(), "The game will crash in " + secondsLeft + " if you continue holding this sequence.", 50, 270, Color.WHITE);

            if (Instant.now().isAfter(this.timer))
                BubbleBlaster.crash(ManualCrashOverlay.createMICLog().createCrash());
        } else {
            this.crashing = false;
        }
    }

    @NotNull
    private static CrashLog createMICLog() {
        var crashLog = new CrashLog("Manually Initiated Crash", new RuntimeException(BSOD));

        BubbleBlaster.getInstance().fillInCrash(crashLog);

        var allStackTraces = Thread.getAllStackTraces();
        allStackTraces.forEach((thread, stackTraceElements) -> {
            var exception = new RuntimeException();
            exception.setStackTrace(stackTraceElements);
            crashLog.addCategory(new CrashCategory("Thread #" + thread.getId() + ": " + thread.getName(), exception));
        });
        return crashLog;
    }

    private void setTimer() {
        this.timer = Instant.now().plusSeconds(15);
    }

    private static boolean isHoldingCrashKey() {
        return Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)
                && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)
                && Gdx.input.isKeyPressed(Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Keys.ALT_RIGHT);
    }
}
