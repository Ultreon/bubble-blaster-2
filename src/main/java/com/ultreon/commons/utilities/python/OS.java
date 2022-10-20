package com.ultreon.commons.utilities.python;

import com.ultreon.bubbles.util.ExceptionUtils;
import com.ultreon.commons.utilities.system.User;

import java.io.IOException;

@Deprecated
@SuppressWarnings("UnusedReturnValue")
public final class OS {
    private OS() {
        throw ExceptionUtils.utilityClass();
    }

    @Deprecated
    public static String getSep() {
        return System.getProperty("file.separator");
    }

    @Deprecated
    public static String getLogin() {
        return System.getProperty("user.name");
    }

    @Deprecated
    public static User getUser() {
        return new User();
    }

    @Deprecated
    public static int system(String cmd) {
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }

        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException ignored) {
            exitCode = -1;
        }

        return exitCode;
    }

    @Deprecated
    public static long getTID() {
        return Thread.currentThread().getId();
    }

    @Deprecated
    public static long getTID(Thread thread) {
        return thread.getId();
    }

    @Deprecated
    public static boolean killThread() {
        return killThread(Thread.currentThread());
    }

    @Deprecated
    public static boolean killThread(long tid) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getId() == tid) {
                t.interrupt();
                return t.isInterrupted();
            }
        }
        return false;
    }

    @Deprecated
    public static boolean killThread(Thread thread) {
        thread.interrupt();
        return thread.isInterrupted();
    }
}
