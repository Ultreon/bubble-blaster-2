package com.ultreon.bubbles.save;

import com.ultreon.bubbles.gamemode.Gamemode;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

@SuppressWarnings({"unused", "FieldCanBeLocal", "SameParameterValue"})
public class GameSave {
    private final String path;
    protected final File gameInfoFile;
    private final File directory;

    protected GameSave(String path) {
        this(new File(path));
    }

    protected GameSave(File directory) {
        this.path = directory.getPath();
        this.directory = directory;
        this.gameInfoFile = new File(path, "info.dat");
    }

    public GameSaveInfo getInfo() throws IOException {
        return new GameSaveInfo(loadInfoData());
    }

    public static GameSave fromFile(File file) {
        return new GameSave(file);
    }

    public static GameSave fromPath(Path file) {
        return new GameSave(file.toFile());
    }

    public static GameSave fromStringPath(String file) {
        return new GameSave(file);
    }

    public CompoundTag loadInfoData() throws IOException {
        return (CompoundTag) NBTUtil.read(gameInfoFile, true).getTag();
    }

    public CompoundTag debugInfoData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", "Test Name - " + new Random().nextInt());
        tag.putLong("saveTime", System.currentTimeMillis());

        return tag;
    }

    public CompoundTag load(String name) throws IOException {
        return load(name, true);
    }

    public CompoundTag load(String name, boolean compressed) throws IOException {
        return this.load(new File(path, name + ".dat"), compressed);
    }

    private CompoundTag load(File file, boolean compressed) throws IOException {
        return (CompoundTag) NBTUtil.read(file, compressed).getTag();
    }

    public void dump(String name, CompoundTag data) throws IOException {
        dump(name, data, true);
    }

    public void dump(String name, CompoundTag data, boolean compressed) throws IOException {
        this.dump(new File(path, name + ".dat"), data, compressed);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private void dump(File file, CompoundTag data, boolean compressed) throws IOException {
        NBTUtil.write(data, file, compressed);
    }

    public File getDirectory() {
        return directory;
    }

    public boolean hasMainState() {
        return hasDataFile("game");
    }

    private boolean hasDataFile(String name) {
        return hasDataFile(new File(path, name + ".bson"));
    }

    private boolean hasDataFile(File file) {
        return file.exists() && file.getAbsolutePath().startsWith(directory.getAbsolutePath());
    }

    public void createFolders(String relPath) throws IOException {
        if (!new File(directory.getPath(), relPath).mkdirs()) {
//            throw new IOException("Failed to create directories " + Path.create(directory.getPath(), relPath).toFile().getAbsolutePath());
        }
    }

    public Gamemode getGamemode() throws IOException {
        return getInfo().getGamemode();
    }

    public long getSeed() throws IOException {
        return getInfo().getSeed();
    }
}
