package com.ultreon.bubbles.common.streams;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;

public class LoggingPrintStream extends PrintStream {
    public LoggingPrintStream(@NotNull OutputStream out) {
        super(out);
    }
}
