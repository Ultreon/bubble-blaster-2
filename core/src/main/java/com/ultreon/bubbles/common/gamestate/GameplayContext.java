package com.ultreon.bubbles.common.gamestate;

import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.gameplay.GameplayStorage;

import java.time.Instant;

public record GameplayContext(Instant time, Environment environment, Gamemode gamemode, GameplayStorage gameplayStorage) {

}
