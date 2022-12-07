package com.ultreon.bubbles.mod.loader;

import java.io.File;

public class ModObject {
    private final ScannerResult scanResult;
    private final ModInfo modInfo;
    Object object;
    String modId;
    File file;

    public ModObject(ScannerResult scanResult, ModInfo modInfo) {
        this.scanResult = scanResult;
        this.modInfo = modInfo;
    }

    public Object getObject() {
        return object;
    }

    public String getModId() {
        return modId;
    }

    public File getFile() {
        return file;
    }

    public ModInfo getModInfo() {
        return modInfo;
    }

    public ScannerResult getScanResult() {
        return scanResult;
    }
}
