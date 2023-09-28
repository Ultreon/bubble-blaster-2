package com.ultreon.bubbles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderable;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.libs.crash.v0.CrashCategory;
import com.ultreon.libs.crash.v0.CrashLog;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Map;

public class ManualCrashOverlay implements Renderable {
    private static final String BSOD = "\n:(\nBubble Blaster ran into a problem and needs to restart.\nStop code: MANUALLY_INITIATED_CRASH\nFile: " + ManualCrashOverlay.class.getSimpleName() + ".class";
    private boolean crashing = false;
    private Instant timer;

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        float width = renderer.getWidth();
        float height = renderer.getHeight();

        if (isHoldingCrashKey() && BubbleBlaster.getInstance().isLoaded()) {
            if (!this.crashing) {
                this.crashing = true;
                this.setTimer();
            }

            long secondsLeft = timer.getEpochSecond() - Instant.now().getEpochSecond();

            renderer.setColor(Color.rgb(0x404040));
            renderer.rect(0, 0, width, height);
            renderer.setColor(Color.CRIMSON);
            renderer.rect(0, 0, (int) width, 10);
            renderer.rect(0, (int) (height - 10), (int) width, 10);

            renderer.drawText(Fonts.DONGLE_140.get(), "Manually Initiating Crash", 50, 60);
            renderer.setColor(Color.rgb(0xffffff));
            renderer.drawText(Fonts.MONOSPACED_24.get(), "You have activated the MIC sequence!", 50, 210);
            renderer.drawText(Fonts.MONOSPACED_24.get(), "If you didn't meant to activate this, stop holding any CTRL/Shift/Alt keys.", 50, 240);
            renderer.drawText(Fonts.MONOSPACED_24.get(), "The game will crash in " + secondsLeft + " if you continue holding this sequence.", 50, 270);

            if (Instant.now().isAfter(this.timer)) {
                BubbleBlaster.crash(createMICLog().createCrash());
            }
        } else {
            this.crashing = false;
        }
    }

    @NotNull
    private static CrashLog createMICLog() {
        CrashLog crashLog = new CrashLog("Manually Initiated Crash", new RuntimeException(BSOD));

        BubbleBlaster.getInstance().fillInCrash(crashLog);

        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
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
