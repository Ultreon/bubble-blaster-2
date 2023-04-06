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
        return details;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(details).append(": \r\n");

        if (entries.size() > 0) {
            ArrayList<AbstractMap.SimpleEntry<String, String>> simpleEntries = new ArrayList<>(entries);
            for (int i = 0; i < simpleEntries.size() - 1; i++) {
                AbstractMap.SimpleEntry<String, String> entry = simpleEntries.get(i);
                sb.append("   ");
                sb.append(entry.getKey());
                sb.append(": ");
                sb.append(entry.getValue());
                sb.append(System.lineSeparator());
            }

            AbstractMap.SimpleEntry<String, String> entry = simpleEntries.get(simpleEntries.size() - 1);
            sb.append("   ");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue());
            sb.append(System.lineSeparator());
        }

        if (throwable != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);

            throwable.printStackTrace(writer);
            writer.flush();

            StringBuffer buffer = stringWriter.getBuffer();
            try {
                stringWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String s = buffer.toString();
            List<String> strings = splitIntoLines(s);
            String join = "   " + StringUtils.join(strings, System.lineSeparator() + "   ");

            sb.append(join);
        }

        sb.append(System.lineSeparator());

        return sb.toString();
    }
}
