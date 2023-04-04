package com.ultreon.premain;

import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.util.Arguments;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BB2GameProvider implements GameProvider {
    private Class<?> clazz;
    private String[] args;

    private final GameTransformer transformer = new GameTransformer();

    @Override
    public String getGameId() {
        return "bubbles";
    }

    @Override
    public String getGameName() {
        return "Bubble Blaster";
    }

    @Override
    public String getRawGameVersion() {
        return "0.1.0";
    }

    @Override
    public String getNormalizedGameVersion() {
        return "0.1.0";
    }

    @Override
    public Collection<BuiltinMod> getBuiltinMods() {
        return List.of(
            new BuiltinMod(List.of(), new BuiltinModMetadata.Builder("bubbles", "0.1.0")
                .addAuthor("Ultreon Team", Map.of(
                        "discord", "https://discord.gg/dtdc46g6ry",
                        "github", "https://github.com/Ultreon",
                        "website", "https://ultreonteam.tk"
                ))
                .addAuthor("Qboi", Map.of(
                        "github", "https://github.com/Qboi123",
                        "website", "https://qboi.tk"
                ))
                .addLicense("Ultreon API License v1.2")
                .build())
        );
    }

    @Override
    public String getEntrypoint() {
        return "com.ultreon.premain.PreMain";
    }

    @Override
    public Path getLaunchDirectory() {
        return Path.of(".");
    }

    @Override
    public boolean isObfuscated() {
        return false;
    }

    @Override
    public boolean requiresUrlClassLoader() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean locateGame(FabricLauncher launcher, String[] args) {
        try {
            this.clazz = Class.forName("com.ultreon.dev.GameDevMain");
            this.args = args;
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void initialize(FabricLauncher launcher) {
        transformer.locateEntrypoints(launcher, new ArrayList<>());
    }

    @Override
    public GameTransformer getEntrypointTransformer() {
        return transformer;
    }

    @Override
    public void unlockClassPath(FabricLauncher launcher) {

    }

    @Override
    public void launch(ClassLoader loader) {
        try {
            Method main = clazz.getDeclaredMethod("main", String[].class);
            main.invoke(null, (Object) args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Arguments getArguments() {
        return new Arguments();
    }

    @Override
    public String[] getLaunchArguments(boolean sanitize) {
        return new String[0];
    }
}
