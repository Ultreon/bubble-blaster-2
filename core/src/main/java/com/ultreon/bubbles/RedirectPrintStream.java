package com.ultreon.bubbles;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharLists;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedirectPrintStream extends PrintStream {
    public RedirectPrintStream(Level level, Logger logger) {
        super(new WriterOutputStream(new RedirectWriter(level, logger), StandardCharsets.UTF_8), true, StandardCharsets.UTF_8);
    }

    private static class RedirectWriter extends Writer {
        private final CharList chars = CharLists.synchronize(new CharArrayList());
        private final List<String > lines = new CopyOnWriteArrayList<>();
        private String cache = null;
        private final Level level;
        private final Logger logger;
        private boolean carriageReturn;
        private final Object lock = new Object();

        public RedirectWriter(Level level, Logger logger) {
            this.level = level;
            this.logger = logger;
        }

        @Override
        public void write(char @NotNull [] cbuf, int off, int len) throws IOException {
            synchronized (lock) {
                char[] dest = new char[len];
                System.arraycopy(cbuf, off, dest, 0, len);
                for (char c : dest) {
                    switch (c) {
                        case '\r' -> {
                            if (carriageReturn) {
                                carriageReturn = false;
                                newLine();
                            }
                            carriageReturn = true;
                        }
                        case '\n' -> {
                            carriageReturn = false;
                            newLine();
                        }
                        default -> {
                            if (carriageReturn) {
                                carriageReturn = false;
                                newLine();
                            }
                            chars.add(c);
                        }
                    }
                }
            }
        }

        private void newLine() {
            lines.add(new String(chars.toCharArray()));
            chars.clear();
        }

        @Override
        public void flush() throws IOException {
            synchronized (lock) {
                for (var line : lines) {
                    logger.log(level, line);
                }
                lines.clear();
            }
        }

        @Override
        public void close() throws IOException {
            synchronized (lock) {
                flush();
                chars.clear();
                lines.clear();
                cache = null;
            }
        }
    }
}
