package com.ultreon.bubbles.data;

import com.ultreon.bubbles.common.GameFolders;
import com.ultreon.data.types.MapType;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public final class GlobalSaveData extends GameData {
    private static GlobalSaveData instance = new GlobalSaveData();
    private double highScore = 0.0;
    private long highScoreTime = 0L;

    public static final File FILE = new File(GameFolders.DATA_DIR, "global.ubo");

    public static GlobalSaveData instance() {
        return instance;
    }

    private GlobalSaveData() {
        instance = this;
    }

    @Override
    protected void load(MapType tag) {
        this.highScore = tag.getDouble("HighScore");
        this.highScoreTime = tag.getLong("HighScoreTime");
    }

    public void load() throws IOException {
        if (!FILE.exists()) {
            this.create();
        }
        this.load(FILE);
    }

    public MapType dump(MapType tag) {
        tag.putDouble("HighScore", this.highScore);
        tag.putLong("HighScoreTime", this.highScoreTime);
        return tag;
    }

    public void dump() throws IOException {
        this.dump(FILE);
    }

    public double getHighScore() {
        return this.highScore;
    }

    public long getHighScoreTime() {
        return this.highScoreTime;
    }

    public void setHighScore(double highScore, long time) {
        if (this.highScore < highScore) {
            this.highScore = highScore;
            this.highScoreTime = time;

            try {
                this.dump();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isCreated() {
        return FILE.exists();
    }

    public boolean isNotCreated() {
        return !FILE.exists();
    }

    public void create() throws IOException {
        if (this.isNotCreated()) {
            this.dump();
        }
    }
}
