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
        public void write(char @NotNull [] cbuf, int off, int len) {
            synchronized (this.lock) {
                char[] dest = new char[len];
                System.arraycopy(cbuf, off, dest, 0, len);
                for (char c : dest) {
                    switch (c) {
                        case '\r' -> {
                            if (this.carriageReturn) {
                                this.carriageReturn = false;
                                this.newLine();
                            }
                            this.carriageReturn = true;
                        }
                        case '\n' -> {
                            this.carriageReturn = false;
                            this.newLine();
                        }
                        default -> {
                            if (this.carriageReturn) {
                                this.carriageReturn = false;
                                this.newLine();
                            }
                            this.chars.add(c);
                        }
                    }
                }
            }
        }

        private void newLine() {
            this.lines.add(new String(this.chars.toCharArray()));
            this.chars.clear();
        }

        @Override
        public void flush() {
            synchronized (this.lock) {
                for (var line : this.lines) {
                    this.logger.log(this.level, line);
                }
                this.lines.clear();
            }
        }

        @Override
        public void close() throws IOException {
            synchronized (this.lock) {
                this.flush();
                this.chars.clear();
                this.lines.clear();
                this.cache = null;
            }
        }
    }
}
