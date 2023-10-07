package com.ultreon.commons.crash;

import com.ultreon.commons.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public final class ApplicationCrash {
    private static final List<Runnable> crashHandlers = new ArrayList<>();
    @NotNull
    private final CrashLog crashLog;

    ApplicationCrash(@NotNull CrashLog crashLog) {
        this.crashLog = crashLog;
    }

    public void printCrash() {
        var crashString = this.crashLog.toString();
        var strings = StringUtils.splitIntoLines(crashString);
        for (var string : strings) {
            System.err.println(string);
        }
    }

    private void crash() {
        for (var handler : crashHandlers) {
            handler.run();
        }
    }

    public static void onCrash(Runnable handler) {
        crashHandlers.add(handler);
    }

    @NotNull
    public CrashLog getCrashLog() {
        return this.crashLog;
    }
}
