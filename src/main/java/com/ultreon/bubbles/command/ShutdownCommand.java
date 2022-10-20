package com.ultreon.bubbles.command;

import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.commons.os.OperatingSystem;

import java.io.IOException;

public class ShutdownCommand implements CommandExecutor {
    protected enum Types {
        SHUTDOWN,
        HIBERNATE,
        SLEEP,
        REBOOT
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Types type = null;
        Integer time = null;

        for (String arg : args) {
            if (arg.equals("-sh") || arg.equals("--shutdown")) {
                if (type != null) {
                    player.sendMessage("Only one type is allowed");
                    return true;
                }

                type = Types.SHUTDOWN;
            } else if (arg.equals("-h") || arg.equals("--hibernate")) {
                if (type != null) {
                    player.sendMessage("Only one type is allowed");
                    return true;
                }

                type = Types.HIBERNATE;
            } else if (arg.equals("-sl") || arg.equals("--sleep")) {
                if (type != null) {
                    player.sendMessage("Only one type is allowed");
                    return true;
                }

                type = Types.SLEEP;
            } else if (arg.equals("-r") || arg.equals("--reboot") || arg.equals("--restart")) {
                if (type != null) {
                    player.sendMessage("Only one type is allowed");
                    return true;
                }

                type = Types.REBOOT;
            } else if (arg.startsWith("-t=")) {
                if (time != null) {
                    player.sendMessage("Only one time is allowed.");
                    return true;
                }

                String timeStr = arg.substring("-t=".length());
                try {
                    time = Integer.parseInt(timeStr);
                } catch (NumberFormatException exception) {
                    player.sendMessage("Invalid number for time: " + timeStr);
                    return true;
                }
            } else if (arg.startsWith("--time=")) {
                if (time != null) {
                    player.sendMessage("Only one time is allowed.");
                    return true;
                }

                String timeStr = arg.substring("--time=".length());
                try {
                    time = Integer.parseInt(timeStr);
                } catch (NumberFormatException exception) {
                    player.sendMessage("Invalid number for time: " + timeStr);
                    return true;
                }
            }
        }

        if (type == null) {
            player.sendMessage("Type is not specified.");
            return false;
        }

        if (time == null) {
            time = 0;
        }

        String cmd;

        if (OperatingSystem.getSystem() == OperatingSystem.WINDOWS) {
            String typeStr;
            switch (type) {
                case SHUTDOWN -> typeStr = "/s";
                case HIBERNATE -> typeStr = "/h";
                case REBOOT -> typeStr = "/r";
                default -> {
                    player.sendMessage("That type is not supported on your operating system.");
                    return true;
                }
            }

            cmd = "shutdown " + typeStr + " /t " + time;
        } else if (OperatingSystem.getSystem() == OperatingSystem.LINUX) {
            if (type == Types.SHUTDOWN) cmd = "shutdown " + time;
            else if (type == Types.REBOOT) cmd = "reboot " + time;
            else {
                player.sendMessage("That type is not supported on your operating system.");
                return true;
            }
        } else {
            player.sendMessage("Your system is not supported.");
            return true;
        }

        player.sendMessage(cmd);

        if (OperatingSystem.getSystem() == OperatingSystem.WINDOWS) {
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
