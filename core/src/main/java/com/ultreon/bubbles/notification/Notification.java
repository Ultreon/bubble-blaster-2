package com.ultreon.bubbles.notification;

import com.ultreon.libs.commons.v0.Mth;

import java.time.Duration;
import java.util.Locale;

public class Notification {
    private static final int MAX_FADE_IN = 500;
    private static final int MAX_FADE_OUT = 500;
    private final String title;
    private final String summary;
    private String subText;
    private final long createTime = System.currentTimeMillis();
    private final long duration;

    public Notification(String title, String summary) {
        this(title, summary, (String)null);
    }

    public Notification(String title, String summary, String subText) {
        this(title, summary, subText, Duration.ofSeconds(3));
    }

    public Notification(String title, String summary, Duration duration) {
        this(title, summary, null, duration);
    }

    public Notification(String title, String summary, String subText, Duration duration) {
        this.title = title;
        this.summary = summary;
        this.subText = subText != null ? subText.toUpperCase(Locale.ROOT) : "";
        this.duration = duration.toMillis();
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getSubText() {
        return subText;
    }

    Notification enableDebug() {
        this.subText = "DEBUG".toUpperCase(Locale.ROOT);
        return this;
    }

    public int getFadeIn() {
        return (int) Math.max(getLifetime(), MAX_FADE_IN);
    }

    public int getFadeOut() {
        return (int) Mth.clamp(getLifetime() - MAX_FADE_IN - (duration - MAX_FADE_OUT), 0, MAX_FADE_OUT);
    }

    public float getMotion() {
        if (getLifetime() < 0)
            return 1f;
        else if (getLifetime() < MAX_FADE_IN)
            return 1f - (float) getLifetime() / MAX_FADE_IN;
        else if (getLifetime() < MAX_FADE_IN + duration)
            return 0f;
        else if (getLifetime() < MAX_FADE_IN + duration + MAX_FADE_OUT)
            return ((float) (getLifetime() - MAX_FADE_IN - duration) / MAX_FADE_OUT);
        else
            return 1f;
    }

    private long getCreateTime() {
        return createTime;
    }

    private long getLifetime() {
        return System.currentTimeMillis() - this.getCreateTime();
    }

    public boolean isDead() {
        return getLifetime() > MAX_FADE_IN + duration + MAX_FADE_OUT;
    }
}
