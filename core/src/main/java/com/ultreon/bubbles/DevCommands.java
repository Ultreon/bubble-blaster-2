package com.ultreon.bubbles;

import com.ultreon.bubbles.entity.player.Player;

public class DevCommands {
    public static void resetHealth(LoadedGame loadedGame) {
        Player player = loadedGame.getGamemode().getPlayer();
        if (player != null) 
            player.setHealth(player.getMaxHealth());
    }
}
