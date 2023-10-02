package com.ultreon.bubbles.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
    private ExceptionUtils() {
        throw ExceptionUtils.utilityClass();
    }

    public static IllegalAccessError utilityClass() {
        return new IllegalAccessError("Tried to initialize utility class.");
    }

    public static String getStackTrace() {
        return ExceptionUtils.getStackTrace(new RuntimeException());
    }

    public static String getStackTrace(String message) {
        return ExceptionUtils.getStackTrace(new RuntimeException(message));
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        String stackTrace = writer.toString();
        printWriter.close();
        return stackTrace;
    }
}
