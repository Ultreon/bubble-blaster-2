package com.ultreon.gameprovider.bubbles;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.*;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.FormattedException;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.GameProviderHelper;
import net.fabricmc.loader.impl.game.LibClassifier;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.metadata.AbstractModMetadata;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.util.Arguments;
import net.fabricmc.loader.impl.util.ExceptionUtil;
import net.fabricmc.loader.impl.util.SystemProperties;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.fabricmc.loader.impl.util.log.LogHandler;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class BB2GameProvider implements GameProvider {
    private static final String[] ALLOWED_EARLY_CLASS_PREFIXES = { "org.apache.logging.log4j.", "com.ultreon.gameprovider.bubbles.", "com.ultreon.premain." };
    private Class<?> clazz;
    private String[] args;

    private final GameTransformer transformer = new GameTransformer();
    private EnvType envType;
    private Arguments arguments;
    private final List<Path> gameJars = new ArrayList<>();
    private final List<Path> logJars = new ArrayList<>();
    private final List<Path> miscGameLibraries = new ArrayList<>();
    private Collection<Path> validParentClassPath = new ArrayList<>();
    private String entrypoint;
    private Path preloaderJar;
    private Path premainJar;
    private Path devJar;
    private boolean log4jAvailable;
    private boolean slf4jAvailable;
    private Path libGdxJar;
    private final Properties versions;

    public BB2GameProvider() {
        InputStream stream = getClass().getResourceAsStream("/versions.properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.versions = properties;
    }

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
        return versions.getProperty("bubbles");
    }

    @Override
    public String getNormalizedGameVersion() {
        return versions.getProperty("bubbles");
    }

    @Override
    public Collection<BuiltinMod> getBuiltinMods() {
        return List.of(
                new BuiltinMod(List.of(libGdxJar), new BuiltinModMetadata.Builder("libgdx", versions.getProperty("libgdx"))
                        .addLicense("Apache-2.0")
                        .addAuthor("libGDX", Map.of("homepage", "http://www.libgdx.com/", "patreon", "https://patreon.com/libgdx", "github", "https://github.com/libgdx", "sources", "https://github.com/libgdx/libgdx"))
                        .addAuthor("Mario Zechner", Map.of("github", "https://github.com/badlogic", "email", "badlogicgames@gmail.com"))
                        .addAuthor("Nathan Sweet", Map.of("github", "https://github.com/NathanSweet", "email", "nathan.sweet@gmail.com"))
                        .addIcon(200, "assets/libgdx/icon.png")
                        .build())
        );
    }

    @Override
    public String getEntrypoint() {
        return entrypoint;
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
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean locateGame(FabricLauncher launcher, String[] args) {
        this.envType = launcher.getEnvironmentType();
        this.arguments = new Arguments();
        arguments.parse(args);

        try {
            var classifier = new LibClassifier<GameLibrary>(GameLibrary.class, envType, this);
            var gameLib = GameLibrary.BB_DESKTOP;
            var gameJar = GameProviderHelper.getCommonGameJar();
            var commonGameJarDeclared = gameJar != null;

            if (commonGameJarDeclared) {
                classifier.process(gameJar);
            }

            classifier.process(launcher.getClassPath());

            gameJar = classifier.getOrigin(GameLibrary.BB_DESKTOP);
            var coreJar = classifier.getOrigin(GameLibrary.BB_CORE);
            this.libGdxJar = classifier.getOrigin(GameLibrary.LIBGDX);

            if (commonGameJarDeclared && gameJar == null) {
                Log.warn(LogCategory.GAME_PROVIDER, "The declared common game jar didn't contain any of the expected classes!");
            }

            if (gameJar != null) {
                gameJars.add(gameJar);
            }

            if (coreJar != null) {
                gameJars.add(coreJar);
            }

            if (libGdxJar != null) {
                gameJars.add(libGdxJar);
            }

            entrypoint = classifier.getClassName(gameLib);
            log4jAvailable = classifier.has(GameLibrary.LOG4J_API) && classifier.has(GameLibrary.LOG4J_CORE);
            slf4jAvailable = classifier.has(GameLibrary.SLF4J_API) && classifier.has(GameLibrary.SLF4J_CORE);
            var hasLogLib = log4jAvailable || slf4jAvailable;

            Log.configureBuiltin(hasLogLib, !hasLogLib);

            for (var lib : GameLibrary.LOGGING) {
                var path = classifier.getOrigin(lib);

                if (path != null) {
                    if (hasLogLib) {
                        logJars.add(path);
                    } else if (!gameJars.contains(path)) {
                        miscGameLibraries.add(path);
                    }
                }
            }

            miscGameLibraries.addAll(classifier.getUnmatchedOrigins());
            validParentClassPath = classifier.getSystemLibraries();
        } catch (IOException e) {
            throw ExceptionUtil.wrap(e);
        }

        // expose obfuscated jar locations for mods to more easily remap code from obfuscated to intermediary
        var share = FabricLoaderImpl.INSTANCE.getObjectShare();
        share.put("fabric-loader:inputGameJar", gameJars.get(0)); // deprecated
        share.put("fabric-loader:inputGameJars", gameJars);

        return true;
    }

    @Override
    public void initialize(FabricLauncher launcher) {
        launcher.setValidParentClassPath(validParentClassPath);

        // Load the logger libraries on the platform CL when in a unit test
        if (!logJars.isEmpty() && !Boolean.getBoolean(SystemProperties.UNIT_TEST)) {
            for (var jar : logJars) {
                if (gameJars.contains(jar)) {
                    launcher.addToClassPath(jar, ALLOWED_EARLY_CLASS_PREFIXES);
                } else {
                    launcher.addToClassPath(jar);
                }
            }
        }

        setupLogHandler(launcher, true);

        transformer.locateEntrypoints(launcher, new ArrayList<>());
    }

    private void setupLogHandler(FabricLauncher launcher, boolean useTargetCl) {
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // lookups are not used by mc and cause issues with older log4j2 versions

        try {
            final var logHandlerClsName = "com.ultreon.gameprovider.bubbles.BB2LogHandler";

            var prevCl = Thread.currentThread().getContextClassLoader();
            Class<?> logHandlerCls;

            if (useTargetCl) {
                Thread.currentThread().setContextClassLoader(launcher.getTargetClassLoader());
                logHandlerCls = launcher.loadIntoTarget(logHandlerClsName);
            } else {
                logHandlerCls = Class.forName(logHandlerClsName);
            }

            Log.init((LogHandler) logHandlerCls.getConstructor().newInstance());
            Thread.currentThread().setContextClassLoader(prevCl);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameTransformer getEntrypointTransformer() {
        return transformer;
    }

    @Override
    public boolean hasAwtSupport() {
        return true;
    }

    @Override
    public void unlockClassPath(FabricLauncher launcher) {
        for (var gameJar : gameJars) {
            if (logJars.contains(gameJar)) {
                launcher.setAllowedPrefixes(gameJar);
            } else {
                launcher.addToClassPath(gameJar);
            }
        }

        for (var lib : miscGameLibraries) {
            launcher.addToClassPath(lib);
        }
    }

    public Path getGameJar() {
        return gameJars.get(0);
    }

    @Override
    public void launch(ClassLoader loader) {
        var targetClass = entrypoint;

        MethodHandle invoker;

        try {
            var c = loader.loadClass(targetClass);
            invoker = MethodHandles.lookup().findStatic(c, "main", MethodType.methodType(void.class, String[].class));
        } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            throw FormattedException.ofLocalized("exception.minecraft.invokeFailure", e);
        }

        try {
            invoker.invokeExact(arguments.toArray());
        } catch (Throwable t) {
            throw FormattedException.ofLocalized("exception.minecraft.generic", t);
        }
    }

    @Override
    public Arguments getArguments() {
        return arguments;
    }

    @Override
    public boolean canOpenErrorGui() {
        if (arguments == null || envType == EnvType.CLIENT) {
            return true;
        }

        var extras = arguments.getExtraArgs();
        return !extras.contains("nogui") && !extras.contains("--nogui");
    }

    @Override
    public String[] getLaunchArguments(boolean sanitize) {
        return new String[0];
    }
}
