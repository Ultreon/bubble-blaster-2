package com.ultreon.bubbles.data;

import com.badlogic.gdx.files.FileHandle;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.GameFolders;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.data.types.MapType;

import java.io.IOException;
import java.time.Instant;

public final class GlobalSaveData extends GameData {
    private static GlobalSaveData instance = new GlobalSaveData();
    private double highScore = 0.0;
    private long highScoreTime = 0L;

    public static final FileHandle FILE = GameFolders.DATA_DIR.child("global.ubo");

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

    @Override
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

    public void setHighScore(double highScore, Instant time) {
        this.highScore = highScore;
        this.highScoreTime = time.toEpochMilli();

        try {
            this.dump();
        } catch (IOException e) {
            BubbleBlaster.LOGGER.error("Failed to save high score:", e);
            var builder = Notification.builder("Error!", "Saving highscore failed!");
            builder.subText("Global Save Data");
        }
    }

    public void updateHighScore(double highScore, Instant time) {
        if (this.highScore < highScore) {
            this.highScore = highScore;
            this.highScoreTime = time.toEpochMilli();

            try {
                this.dump();
            } catch (IOException e) {
                BubbleBlaster.LOGGER.error("Failed to save high score:", e);
                var builder = Notification.builder("Error!", "Saving highscore failed!");
                builder.subText("Global Save Data");
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
