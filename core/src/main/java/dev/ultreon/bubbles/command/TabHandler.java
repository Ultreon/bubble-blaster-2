package dev.ultreon.bubbles.command;

import java.util.List;

public interface TabHandler {
    List<String> tabComplete(String[] args);
}
