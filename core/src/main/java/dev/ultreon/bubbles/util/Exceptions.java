package dev.ultreon.bubbles.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Exceptions {
    @Deprecated(forRemoval = true)
    public static IllegalAccessError utilityClass() {
        return new IllegalAccessError("Tried to initialize utility class.");
    }

    public static String getStackTrace() {
        return Exceptions.getStackTrace(new RuntimeException());
    }

    public static String getStackTrace(String message) {
        return Exceptions.getStackTrace(new RuntimeException(message));
    }

    public static String getStackTrace(Throwable throwable) {
        var writer = new StringWriter();
        var printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        var stackTrace = writer.toString();
        printWriter.close();
        return stackTrace;
    }
}
