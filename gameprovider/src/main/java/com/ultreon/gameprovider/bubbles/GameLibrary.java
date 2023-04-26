/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ultreon.gameprovider.bubbles;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.impl.game.LibClassifier.LibraryType;

enum GameLibrary implements LibraryType {
	BB_DESKTOP("com/ultreon/bubbles/DesktopLauncher.class"),
	BB_CORE("com/ultreon/bubbles/game/BubbleBlaster.class"),
	BB_DEV("com/ultreon/dev/GameDevMain.class"),
	BB_PRELOADER("com/ultreon/gameprovider/bubbles/PreGameLoader.class"),
	BB_PREMAIN("com/ultreon/premain/PreMain.class"),
	LIBGDX("com/badlogic/gdx/Gdx.class"),
	LOG4J_API("org/apache/logging/log4j/LogManager.class"),
	LOG4J_CORE("META-INF/services/org.apache.logging.log4j.spi.Provider", "META-INF/log4j-provider.properties"),
	LOG4J_CONFIG("log4j2.xml"),
	GSON("com/google/gson/TypeAdapter.class"), // used by log4j plugins
	SLF4J_API("org/slf4j/Logger.class"),
	SLF4J_CORE("META-INF/services/org.slf4j.spi.SLF4JServiceProvider");

	static final GameLibrary[] GAME = { BB_DESKTOP, BB_CORE, BB_DEV, BB_PRELOADER, BB_PREMAIN };
	static final GameLibrary[] LOGGING = { LOG4J_API, LOG4J_CORE, LOG4J_CONFIG, GSON, SLF4J_API, SLF4J_CORE };

	private final EnvType env;
	private final String[] paths;

	GameLibrary(String path) {
		this(null, new String[] { path });
	}

	GameLibrary(String... paths) {
		this(null, paths);
	}

	GameLibrary(EnvType env, String... paths) {
		this.paths = paths;
		this.env = env;
	}

	@Override
	public boolean isApplicable(EnvType env) {
		return this.env == null || this.env == env;
	}

	@Override
	public String[] getPaths() {
		return paths;
	}
}
