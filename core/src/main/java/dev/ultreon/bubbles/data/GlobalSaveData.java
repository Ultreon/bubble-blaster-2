package dev.ultreon.bubbles.data;

import com.badlogic.gdx.files.FileHandle;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.common.GameFolders;
import dev.ultreon.bubbles.entity.Bubble;
import dev.ultreon.bubbles.notification.Notification;
import dev.ultreon.ubo.types.MapType;

import java.io.IOException;
import java.time.Instant;

public final class GlobalSaveData extends GameData {
    private static GlobalSaveData instance = new GlobalSaveData();
    private double highScore = 0.0;
    private long highScoreTime = 0L;

    public static final FileHandle FILE = GameFolders.DATA_DIR.child("global.ubo");
    private double money;
    private double xp;
    private int xpLevel;

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
        this.money = tag.getDouble("Money");
        this.xp = tag.getDouble("XP");
        this.xpLevel = tag.getInt("XPLevel");
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
        tag.putDouble("Money", this.money);
        tag.putDouble("XP", this.xp);
        tag.putInt("XPLevel", this.xpLevel);
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

    public double getMoney() {
        return this.money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getXp() {
        return this.xp;
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    public int getXpLevel() {
        return this.xpLevel;
    }

    public void setXpLevel(int xpLevel) {
        this.xpLevel = xpLevel;
    }

    public void reset() {
        this.money = 0;
        this.xp = 0;
        this.xpLevel = 0;
        this.highScore = 0;
        this.highScoreTime = 0;
    }
}
