package com.ultreon.bubbles.platform.android;

import android.content.Intent;
import android.os.Looper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ultreon.bubbles.*;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.commons.os.OperatingSystem;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.commons.v0.ProgressMessenger;
import com.ultreon.libs.crash.v0.CrashLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class AndroidPlatform extends GamePlatform {
    private final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();
    private final AndroidLauncher launcher;

    public AndroidPlatform(AndroidLauncher launcher) {
        super();
        this.launcher = launcher;
    }

    @Override
    public void create() {
        Looper.prepare();
    }

    @Override
    public FileHandle data(String path) {
        return Gdx.files.local(path);
    }

    @Override
    public Logger getLogger(String name) {
        return this.loggers.computeIfAbsent(name, s -> new AndroidLogger(name));
    }

    @Override
    public OperatingSystem getOperatingSystem() {
        return OperatingSystem.Android;
    }

    @Override
    public void handleCrash(CrashLog crashLog) {
        super.handleCrash(crashLog);

        var intent = new Intent(this.launcher, CrashActivity.class);
        intent.putExtra("CrashLog", crashLog.toString());
        this.launcher.startActivity(intent);
    }

    @Override
    public FileHandle getDataDirectory() {
        return Gdx.files.local(".");
    }

    @Override
    public void loadGameResources(AtomicReference<ProgressMessenger> progressAlt, Messenger msgAlt) {
        this.game().getResourceManager().importDeferredPackage(BubbleBlaster.class);
    }

    @Override
    public String getGameVersion() {
        return "0.1.0";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return false;
    }

    @Override
    public Screen openModListScreen() {
        return null;
    }

    @Override
    public void loadModResources(AtomicReference<ProgressMessenger> progressAlt, Messenger msgAlt) {

    }

    public AndroidLauncher getLauncher() {
        return this.launcher;
    }

    @Override
    public void showError(@NotNull String title, @Nullable String description) {
        this.launcher.showMessage(title, description);
    }
}
