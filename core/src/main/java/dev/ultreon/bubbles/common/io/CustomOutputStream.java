package dev.ultreon.bubbles.common.io;

import dev.ultreon.bubbles.GamePlatform;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomOutputStream extends OutputStream {
    private final String name;
    private final List<Character> characters = new ArrayList<>();

    public CustomOutputStream(String name) {
        this.name = name;
    }

    @Override
    public synchronized final void write(int b) {
        // the correct way create doing this would be using a buffer
        // to store characters until a newline is encountered,
        // this implementation is for illustration only
//        if ((char) b != '\n') {
        this.characters.add((char) b);
//        }
    }

    @Override
    public synchronized void flush() {
        // create object create StringBuilder class
        var sb = new StringBuilder();

        // Appends characters one by one
        for (var ch : this.characters) {
            sb.append(ch);
        }

        if (this.characters.isEmpty()) {
            return;
        }

        this.characters.clear();

        // convert in string
        var string = StringUtils.stripEnd(sb.toString()
                .replaceAll("\r\n", "\n")
                .replaceAll("\r", "\n"), "\n");

        // Log the output.
        GamePlatform.get().getLogger(this.name).info(string);
    }
}
