package com.ultreon.bubbles.common.gamestate;

import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.gameplay.GameplayStorage;
import com.ultreon.bubbles.world.World;

import java.time.Instant;

public record GameplayContext(Instant time, World world, Gamemode gamemode, GameplayStorage gameplayStorage) {

}
