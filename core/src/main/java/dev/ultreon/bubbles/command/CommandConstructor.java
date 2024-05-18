package dev.ultreon.bubbles.command;

import dev.ultreon.bubbles.entity.player.Player;

import java.util.HashMap;

public final class CommandConstructor {
    private static final HashMap<String, CommandExecutor> commands = new HashMap<>();

    public static void add(String name, CommandExecutor handler) {
        commands.put(name, handler);
    }

    public static CommandExecutor get(String name) {
        if (commands.containsKey(name)) {
            return commands.get(name);
        }

        return null;
    }

    public static boolean execute(String name, Player player, String[] args) {
        var handler = CommandConstructor.get(name);

        if (handler == null) {
            return false;
        }

        try {
            handler.execute(player, args);
        } catch (Throwable throwable) {
            player.sendSystemMessage("Error occurred when executing the command.");
            player.sendSystemMessage("  See the log for more information.");
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
}
