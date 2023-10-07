package com.ultreon.commons.crash;

import com.ultreon.commons.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static com.ultreon.commons.util.StringUtils.splitIntoLines;

@Deprecated
public class CrashCategory {
    protected final List<AbstractMap.SimpleEntry<String, String>> entries = new ArrayList<>();
    protected final String details;
    protected Throwable throwable;

    public CrashCategory(String details) {
        this(details, null);
    }

    public CrashCategory(String details, Throwable t) {
        this.details = details;
        this.throwable = t;
    }

    public void add(String key, @Nullable Object value) {
        if (key.contains(":")) {
            throw new IllegalArgumentException("Key cannot contain a colon");
        }

        if (key.length() > 32) {
            throw new IllegalArgumentException("Key cannot be longer than 32 characters.");
        }

        this.entries.add(new AbstractMap.SimpleEntry<>(key, value != null ? value.toString() : "null@0"));
    }

    public String getDetails() {
        return this.details;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public String toString() {
        var sb = new StringBuilder();
        sb.append(this.details).append(": \r\n");

        if (!this.entries.isEmpty()) {
            var simpleEntries = new ArrayList<AbstractMap.SimpleEntry<String, String>>(this.entries);
            for (var i = 0; i < simpleEntries.size() - 1; i++) {
                var entry = simpleEntries.get(i);
                sb.append("   ");
                sb.append(entry.getKey());
                sb.append(": ");
                sb.append(entry.getValue());
                sb.append(System.lineSeparator());
            }

            var entry = simpleEntries.get(simpleEntries.size() - 1);
            sb.append("   ");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue());
            sb.append(System.lineSeparator());
        }

        if (this.throwable != null) {
            var stringWriter = new StringWriter();
            var writer = new PrintWriter(stringWriter);

            this.throwable.printStackTrace(writer);
            writer.flush();

            var buffer = stringWriter.getBuffer();
            try {
                stringWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            var s = buffer.toString();
            var strings = splitIntoLines(s);
            var join = "   " + StringUtils.join(strings, System.lineSeparator() + "   ");

            sb.append(join);
        }

        sb.append(System.lineSeparator());

        return sb.toString();
    }
}
