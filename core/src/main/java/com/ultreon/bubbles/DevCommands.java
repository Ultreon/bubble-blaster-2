package com.ultreon.bubbles;

public class DevCommands {
    public static void resetHealth(LoadedGame loadedGame) {
        var player = loadedGame.getGamemode().getPlayer();
        if (player != null) 
            player.setHealth(player.getMaxHealth());
    }
}
