package com.ultreon.bubbles;

import com.badlogic.gdx.Gdx;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.activity.Activity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static com.ultreon.bubbles.BubbleBlaster.LOGGER;

public class DiscordRPC {
    private final Lock updateLock = new ReentrantLock(true);
    private final Thread thread;
    private final String gameVersion;
    private boolean running = true;
    private boolean crashed = false;
    private boolean dirty = true;
    private Supplier<Activity> activity;
    private Activity currentActivity;

    public DiscordRPC() {
        this.setActivity(() -> {
            Activity activity = new Activity();
            activity.setState("Loading the game...");
            return activity;
        });

        this.gameVersion = BubbleBlaster.getGameVersion().getFriendlyString();

        this.thread = new Thread(this::run, "DiscordRPC");
        this.thread.setDaemon(true);
        this.thread.setPriority(Thread.MIN_PRIORITY);
        this.thread.start();
    }

    private void run() {
        if (!this.download()) return;

        while (running) {
            // Set parameters for the Core
            try (var params = new CreateParams()) {
                params.setClientID(933147296311427144L);
                params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);

                // Create the Core
                try (var core = new Core(params)) {
                    if (this.callbackLoop(core)) break;
                }
            }
        }
    }

    /**
     * Download the game sdk.
     *
     * @return true if the download was successful.
     */
    private boolean download() {
        try {
            var sdkDownloader = new DiscordSDKDownloader();
            sdkDownloader.downloadSync();

            if (sdkDownloader.isFailed()) {
                System.err.println("Failed to download the Discord SDK");
                return false;
            }

            var sdkFile = sdkDownloader.getFile();
            if (sdkFile == null || !sdkFile.exists()) {
                System.err.println("Discord SDK file is missing");
                return false;
            }

            // Initialize the Core
            Core.init(sdkFile);
        } catch (Exception e) {
            LOGGER.warn("Discord RPC failed to initialize:", e);
            return false;
        }

        return true;
    }

    /**
     * Callback loop for the Discord RPC.
     *
     * @param core the game sdk core.
     * @return true if the RPC crashed.
     */
    @SuppressWarnings("BusyWait")
    private boolean callbackLoop(Core core) {
        while (running) {
            try {
                try {
                    core.runCallbacks();
                } catch (GameSDKException e) {
                    Thread.sleep(5000);
                    return true;
                }

                updateRpc(core);

                if (crashed) {
                    return true;
                }

                // Sleep a bit to save CPU
                Thread.sleep(16);
            } catch (InterruptedException e) {
                try {
                    core.close();
                } catch (Exception ex) {
                    return false;
                }
                return false;
            } catch (Throwable t) {
                LOGGER.warn("Crash occurred in Discord RPC:", t);
                try {
                    core.close();
                } catch (Exception ex) {
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Update RPC activity if changed.
     *
     * @param core the game sdk core.
     */
    private void updateRpc(Core core) {
        this.updateLock.lock();
        if (this.dirty) {
            this.dirty = false;
            Activity activity = this.activity.get();
            core.activityManager().updateActivity(activity, this::handleResult);
            this.currentActivity.close();
            this.currentActivity = activity;
        }
        this.updateLock.unlock();
    }

    private boolean isCrashResult(Result result) {
        return result == Result.NOT_AUTHENTICATED
                || result == Result.INVALID_ACCESS_TOKEN
                || result == Result.INVALID_PERMISSIONS
                || result == Result.INVALID_VERSION
                || result == Result.INTERNAL_ERROR
                || result == Result.INVALID_COMMAND
                || result == Result.INVALID_CHANNEL
                || result == Result.INVALID_EVENT
                || result == Result.INVALID_PAYLOAD
                || result == Result.INVALID_ORIGIN;
    }

    public void stop() {
        this.running = false;
        this.thread.interrupt();
    }

    public void setActivity(Supplier<Activity> activity) {
        this.updateLock.lock();
        this.activity = () -> {
            Activity ret = activity.get();
            String state = ret.getState();
            Gdx.graphics.setTitle("Bubble Blaster 2 - " + this.gameVersion + " - " + state);
            ret.assets().setLargeImage("icon");
            ret.assets().setLargeText(this.gameVersion);
            return ret;
        };
        this.dirty = true;
        this.updateLock.unlock();
    }

    public void join() throws InterruptedException {
        this.thread.join();
    }

    public Activity getActivity() {
        return currentActivity;
    }

    private void handleResult(Result result) {
        if (this.isCrashResult(result)) {
            this.crashed = true;
        }
    }
}
