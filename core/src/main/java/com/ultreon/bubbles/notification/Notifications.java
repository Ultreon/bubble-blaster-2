package com.ultreon.bubbles.notification;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderable;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.libs.datetime.v0.Duration;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Notifications implements Renderable {
    private static final int NOTIFICATION_HEIGHT = 80;
    private static final int NOTIFICATION_WIDTH = 300;
    private static final int NOTIFICATION_OFFSET = 20;
    private static final int NOTIFICATION_GAP = 10;

    private final Lock lock = new ReentrantLock(true);
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final Deque<Notification> notifications = new ArrayDeque<>();
    private final Set<UUID> usedNotifications = new HashSet<>();

    public Notifications() {

    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        if (this.game.isLoading()) return;

        var x = this.game.getWidth() - NOTIFICATION_OFFSET - NOTIFICATION_WIDTH;
        var y = NOTIFICATION_OFFSET;

        this.lock.lock();
        this.notifications.removeIf(Notification::isDead);
        for (var notification : this.notifications) {
            var title = notification.getTitle();
            var summary = notification.getSummary();
            var subText = notification.getSubText();
            var motionRatio = notification.getMotion();
            var motion = (NOTIFICATION_WIDTH + NOTIFICATION_OFFSET) * motionRatio;

            renderer.fill(x + motion, y, NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT, Color.rgb(0x101010));
            renderer.box(x + motion + 5, y + 5, NOTIFICATION_WIDTH - 10, NOTIFICATION_HEIGHT - 10, Color.rgb(0x505050));

            renderer.drawText(Fonts.SANS_HEADER_3.get(), title, x + motion + 10, y + 13, Color.rgb(0xd0d0d0));
            renderer.drawText(Fonts.SANS_PARAGRAPH.get(), summary, x + motion + 10, y + 40, Color.rgb(0xb0b0b0));
            renderer.drawText(Fonts.SANS_SUBTITLE.get(), subText == null ? "" : subText, x + motion + 10, y + 60, Color.rgb(0x707070));

            y += NOTIFICATION_HEIGHT + NOTIFICATION_GAP;
        }
        this.lock.unlock();
    }

    public void notify(Notification notification) {
        if (!BubbleBlaster.isOnRenderingThread()) {
            BubbleBlaster.invoke(() -> this.notify(notification));
            return;
        }

        this.notifications.addLast(notification);
    }

    public void notifyOnce(UUID uuid, Notification message) {
        if (!this.usedNotifications.contains(uuid)) {
            this.usedNotifications.add(uuid);
            this.notify(message);
        }
    }

    public void unavailable(String feature) {
        this.notify(Notification.builder("Unavailable Feature", String.format("'%s' isn't available yet.", feature))
                .subText("Feature Locker")
                .duration(Duration.ofSeconds(5))
                .build()
        );
    }
}
