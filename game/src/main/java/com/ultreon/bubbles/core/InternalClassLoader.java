package com.ultreon.bubbles.core;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.mod.loader.Scanner;
import com.ultreon.bubbles.mod.loader.ScannerResult;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalClassLoader extends URLClassLoader {
    private static final String INTERNAL_ID = "com.ultreon.bubbleblaster";
    private final Map<String, ScannerResult> scans = new HashMap<>();

    private static InternalClassLoader instance = new InternalClassLoader();
    private final URL gameFile;
    private ScannerResult scanResult;

    public InternalClassLoader() {
        super(new URL[]{BubbleBlaster.class.getProtectionDomain().getCodeSource().getLocation()}, BubbleBlaster.class.getClassLoader());
        this.gameFile = BubbleBlaster.getJarUrl();
        if (instance != null) {
            throw new IllegalStateException("Game class loader already initialized.");
        }
        instance = this;
    }

    public static InternalClassLoader get() {
        return instance;
    }

    public ScannerResult scan() {
        Scanner scanner = new Scanner(true, List.of(gameFile), this);
        ScannerResult scanResult = scanner.scan();
        this.scans.put(INTERNAL_ID, scanResult);
        return this.scanResult = scanResult;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    protected Class<?> findClass(String moduleName, String name) {
        return super.findClass(moduleName, name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    public ScannerResult getResult(String modFileId) {
        return scans.get(modFileId);
    }

    public ScannerResult getScanResult() {
        return scanResult;
    }

    public URL getGameFile() {
        return gameFile;
    }
}