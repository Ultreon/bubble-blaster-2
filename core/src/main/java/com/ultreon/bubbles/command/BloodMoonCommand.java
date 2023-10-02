package com.ultreon.bubbles.command;

import com.ultreon.bubbles.entity.player.Player;

public class BloodMoonCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        player.getWorld().triggerBloodMoon();
        return true;
    }
}
