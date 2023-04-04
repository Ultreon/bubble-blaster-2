package com.ultreon.premain;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.FormattedException;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.GameProviderHelper;
import net.fabricmc.loader.impl.game.LibClassifier;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.util.Arguments;
import net.fabricmc.loader.impl.util.ExceptionUtil;
import net.fabricmc.loader.impl.util.SystemProperties;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogHandler;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldCanBeLocal")
public class BB2GameProvider implements GameProvider {
    private static final String[] ALLOWED_EARLY_CLASS_PREFIXES = { "org.apache.logging.log4j.", "com.ultreon.preloader.", "com.ultreon.premain." };
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
        return true;
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
            var classifier = new LibClassifier<>(GameLibrary.class, envType, this);
            var gameLib = GameLibrary.BB_MAIN;
            var preloaderLib = GameLibrary.BB_PRELOADER;
            var gameJar = GameProviderHelper.getCommonGameJar();

            if (gameJar != null) {
                classifier.process(gameJar);
            }

            if (gameJar == null) gameJar = classifier.getOrigin(GameLibrary.BB_MAIN);

            classifier.process(launcher.getClassPath());

            preloaderJar = classifier.getOrigin(GameLibrary.BB_PRELOADER);
            premainJar = classifier.getOrigin(GameLibrary.BB_PREMAIN);
            devJar = classifier.getOrigin(GameLibrary.BB_DEV);

            gameJar = classifier.getOrigin(gameLib);
            if (gameJar == null) return false;

            gameJars.add(gameJar);

            if (!gameJar.equals(preloaderJar)) {
                System.out.println("preloaderJar = " + preloaderJar);
                gameJars.add(preloaderJar);
            }

            entrypoint = classifier.getClassName(preloaderLib);
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
            System.out.println("miscGameLibraries = " + miscGameLibraries);
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
            for (Path jar : logJars) {
                if (gameJars.contains(jar)) {
                    launcher.addToClassPath(jar, ALLOWED_EARLY_CLASS_PREFIXES);
                } else {
                    launcher.addToClassPath(jar);
                }
            }
        }

        transformer.locateEntrypoints(launcher, new ArrayList<>());
    }

    private void setupLogHandler(FabricLauncher launcher, boolean useTargetCl) {
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // lookups are not used by mc and cause issues with older log4j2 versions

        try {
            final String logHandlerClsName;

            if (log4jAvailable) {
                logHandlerClsName = "net.fabricmc.loader.impl.game.minecraft.Log4jLogHandler";
            } else if (slf4jAvailable) {
                logHandlerClsName = "net.fabricmc.loader.impl.game.minecraft.Slf4jLogHandler";
            } else {
                return;
            }

            ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
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
//            if (gameJar.toString().contains("fabric-loader")) {
//                System.out.println("lib = " + lib);
//            }
        }

        for (var lib : miscGameLibraries) {
            if (lib.toString().contains("fabric-loader")) {
                System.out.println("lib = " + lib);
            }
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
