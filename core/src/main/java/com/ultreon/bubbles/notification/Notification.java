package com.ultreon.bubbles.notification;

import com.ultreon.libs.commons.v0.Mth;

public class Notification {
    private static final int MAX_FADE_IN = 500;
    private static final int MAX_LIFETIME = 3000;
    private static final int MAX_FADE_OUT = 500;
    private final String title;
    private final String summary;
    private final long createTime = System.currentTimeMillis();
    private String subText;

    public Notification(String title, String summary) {
        this(title, summary, null);
    }

    public Notification(String title, String summary, String subText) {
        this.title = title;
        this.summary = summary;
        this.subText = subText;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public int getFadeIn() {
        return (int) Math.max(getLifetime(), MAX_FADE_IN);
    }

    public int getFadeOut() {
        return (int) Mth.clamp(getLifetime() - MAX_FADE_IN - (MAX_LIFETIME - MAX_FADE_OUT), 0, MAX_FADE_OUT);
    }

    public float getMotion() {
        if (getLifetime() < 0)
            return 1f;
        else if (getLifetime() < MAX_FADE_IN)
            return 1f - (float) getLifetime() / MAX_FADE_IN;
        else if (getLifetime() < MAX_FADE_IN + MAX_LIFETIME)
            return 0f;
        else if (getLifetime() < MAX_FADE_IN + MAX_LIFETIME + MAX_FADE_OUT)
            return ((float) (getLifetime() - MAX_FADE_IN - MAX_LIFETIME) / MAX_FADE_OUT);
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
        return getLifetime() > MAX_FADE_IN + MAX_LIFETIME + MAX_FADE_OUT;
    }

    public String getSubText() {
        return subText;
    }
}
