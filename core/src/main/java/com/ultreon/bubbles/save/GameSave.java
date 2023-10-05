package com.ultreon.bubbles.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.data.DataIo;
import com.ultreon.data.types.MapType;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Random;

@SuppressWarnings({"unused", "SameParameterValue"})
public class GameSave {
    public static final Marker MARKER = MarkerFactory.getMarker("GameSaves");
    private final String path;
    protected final File gameInfoFile;
    private final FileHandle handle;

    @Deprecated
    protected GameSave(String path) {
        this(Gdx.files.absolute(path));
    }

    protected GameSave(FileHandle handle) {
        this.path = handle.path();
        this.handle = handle;
        this.gameInfoFile = new File(this.path, "info.ubo");
    }

    public GameSaveInfo getInfo() throws IOException {
        return new GameSaveInfo(this.loadInfoData());
    }

    public static GameSave fromFile(FileHandle file) {
        return new GameSave(file);
    }

    @Deprecated
    public static GameSave fromPath(Path file) {
        return new GameSave(file.toAbsolutePath().toString());
    }

    @Deprecated
    public static GameSave fromStringPath(String file) {
        return new GameSave(file);
    }

    public MapType loadInfoData() throws IOException {
        return DataIo.readCompressed(this.gameInfoFile);
    }

    public MapType debugInfoData() {
        MapType tag = new MapType();
        tag.putString("name", "Test Name - " + new Random().nextInt());
        tag.putLong("saveTime", System.currentTimeMillis());

        return tag;
    }

    public MapType load(String name) throws IOException {
        return this.load(name, true);
    }

    public MapType load(String name, boolean compressed) throws IOException {
        return this.load(new File(this.path, name + ".ubo"), compressed);
    }

    private MapType load(File file, boolean compressed) throws IOException {
        if (compressed) {
            return DataIo.readCompressed(file);
        }
        return DataIo.read(file);
    }

    public void dump(String name, MapType data) throws IOException {
        this.dump(name, data, true);
    }

    public void dump(String name, MapType data, boolean compressed) throws IOException {
        this.dump(this.handle.child(name + ".ubo"), data, compressed);
    }

    private void dump(FileHandle file, MapType data, boolean compressed) throws IOException {
        if (compressed) {
            try (OutputStream output = file.write(false)) {
                DataIo.writeCompressed(data, output);
            }
            return;
        }
        try (OutputStream output = file.write(false)) {
            DataIo.write(data, output);
        }
    }

    public FileHandle getHandle() {
        return this.handle;
    }

    public boolean hasMainState() {
        return this.hasDataFile("game");
    }

    public boolean hasDataFile(String name) {
        return this.handle.child(name + ".bson").exists();
    }

    @CanIgnoreReturnValue
    public void delete() throws IOException {
        if (this.handle.isDirectory()) this.handle.deleteDirectory();
        else this.handle.delete();
    }

    public void createFolders(String relPath) throws IOException {
        FileHandle file = this.handle.child(relPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public Gamemode getGamemode() throws IOException {
        return this.getInfo().getGamemode();
    }

    public Difficulty getDifficulty() throws IOException {
        return this.getInfo().getDifficulty();
    }

    public long getSeed() throws IOException {
        return this.getInfo().getSeed();
    }

    @Override
    public String toString() {
        return "GameSave{" +
                "path='" + this.path + '\'' +
                '}';
    }
}
