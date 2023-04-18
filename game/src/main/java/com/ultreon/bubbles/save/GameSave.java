package com.ultreon.bubbles.save;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.commons.util.FileUtils;
import com.ultreon.data.DataIo;
import com.ultreon.data.types.MapType;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

@SuppressWarnings({"unused", "FieldCanBeLocal", "SameParameterValue"})
public class GameSave {
    public static final Marker MARKER = MarkerFactory.getMarker("GameSaves");
    private final String path;
    protected final File gameInfoFile;
    private final File directory;

    protected GameSave(String path) {
        this(new File(path));
    }

    protected GameSave(File directory) {
        this.path = directory.getPath();
        this.directory = directory;
        this.gameInfoFile = new File(path, "info.ubo");
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

    public MapType loadInfoData() throws IOException {
        return DataIo.readCompressed(gameInfoFile);
    }

    public MapType debugInfoData() {
        MapType tag = new MapType();
        tag.putString("name", "Test Name - " + new Random().nextInt());
        tag.putLong("saveTime", System.currentTimeMillis());

        return tag;
    }

    public MapType load(String name) throws IOException {
        return load(name, true);
    }

    public MapType load(String name, boolean compressed) throws IOException {
        return this.load(new File(path, name + ".ubo"), compressed);
    }

    private MapType load(File file, boolean compressed) throws IOException {
        if (compressed) {
            return DataIo.readCompressed(file);
        }
        return DataIo.read(file);
    }

    public void dump(String name, MapType data) throws IOException {
        dump(name, data, true);
    }

    public void dump(String name, MapType data, boolean compressed) throws IOException {
        this.dump(new File(path, name + ".ubo"), data, compressed);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private void dump(File file, MapType data, boolean compressed) throws IOException {
        if (compressed) {
            DataIo.writeCompressed(data, file);
            return;
        }
        DataIo.write(data, file);
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

    @CanIgnoreReturnValue
    public void delete() throws IOException {
        FileUtils.deleteDir(directory);
    }

    public void createFolders(String relPath) throws IOException {
        File file = new File(directory.getPath(), relPath);
        if (!file.mkdirs()) {
            if (!file.exists()) {
                throw new IOException("Failed to create directories " + Path.of(directory.getPath(), relPath).toFile().getAbsolutePath());
            }
        }
    }

    public Gamemode getGamemode() throws IOException {
        return getInfo().getGamemode();
    }

    public long getSeed() throws IOException {
        return getInfo().getSeed();
    }
}
