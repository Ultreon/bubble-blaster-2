package com.ultreon.bubbles;

import com.ultreon.libs.crash.v0.CrashLog;

class GameExceptions implements Thread.UncaughtExceptionHandler {
    GameExceptions() {
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        var crashLog = new CrashLog("Uncaught exception", throwable);
        BubbleBlaster.crash(crashLog.createCrash());
    }
}
