package com.ultreon.bubbles.data;

import com.ultreon.bubbles.common.References;
import com.ultreon.data.types.MapType;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public final class GlobalSaveData extends GameData {
    private static GlobalSaveData instance = new GlobalSaveData();
    private double highScore = 0.0;
    private long highScoreTime = 0L;

    public static final File FILE = new File(References.GAME_DIR, "global.ubo");

    public static GlobalSaveData instance() {
        return instance;
    }

    private GlobalSaveData() {
        instance = this;
    }

    @Override
    protected void load(MapType tag) {
        highScore = tag.getDouble("HighScore");
        highScoreTime = tag.getLong("HighScoreTime");
    }

    public void load() throws IOException {
        if (!FILE.exists()) {
            create();
        }
        this.load(FILE);
    }

    public MapType dump(MapType tag) {
        tag.putDouble("HighScore", highScore);
        tag.putLong("HighScoreTime", highScoreTime);
        return tag;
    }

    public void dump() throws IOException {
        dump(FILE);
    }

    public double getHighScore() {
        return highScore;
    }

    public long getHighScoreTime() {
        return highScoreTime;
    }

    public void setHighScore(double highScore, long time) {
        if (this.highScore < highScore) {
            this.highScore = highScore;
            this.highScoreTime = time;

            try {
                dump();
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
        if (isNotCreated()) {
            dump();
        }
    }
}
