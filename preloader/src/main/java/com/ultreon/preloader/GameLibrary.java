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

package com.ultreon.preloader;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.impl.game.LibClassifier.LibraryType;

enum GameLibrary implements LibraryType {
	BB_MAIN("com/ultreon/bubbles/Main.class"),
	BB_DEV("com/ultreon/dev/GameDevMain.class"),
	BB_PRELOADER("com/ultreon/preloader/PreGameLoader.class"),
	BB_PREMAIN("com/ultreon/premain/PreMain.class"),
	LOG4J_API("org/apache/logging/log4j/LogManager.class"),
	LOG4J_CORE("META-INF/services/org.apache.logging.log4j.spi.Provider", "META-INF/log4j-provider.properties"),
	LOG4J_CONFIG("log4j2.xml"),
	LOG4J_PLUGIN("com/mojang/util/UUIDTypeAdapter.class"), // in authlib
	LOG4J_PLUGIN_2("com/mojang/patchy/LegacyXMLLayout.class"), // in patchy
	LOG4J_PLUGIN_3("net/minecrell/terminalconsole/util/LoggerNamePatternSelector.class"), // in terminalconsoleappender, used by loom's log4j config
	GSON("com/google/gson/TypeAdapter.class"), // used by log4j plugins
	SLF4J_API("org/slf4j/Logger.class"),
	SLF4J_CORE("META-INF/services/org.slf4j.spi.SLF4JServiceProvider");

	static final GameLibrary[] GAME = { BB_MAIN, BB_DEV, BB_PRELOADER, BB_PREMAIN };
	static final GameLibrary[] LOGGING = { LOG4J_API, LOG4J_CORE, LOG4J_CONFIG, LOG4J_PLUGIN, LOG4J_PLUGIN_2, LOG4J_PLUGIN_3, GSON, SLF4J_API, SLF4J_CORE };

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
