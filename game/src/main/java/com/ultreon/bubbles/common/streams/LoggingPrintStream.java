package com.ultreon.bubbles.common.streams;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.OutputStream;
import java.io.PrintStream;

public class LoggingPrintStream extends PrintStream {
    public LoggingPrintStream(@NonNull OutputStream out) {
        super(out);
    }
}
