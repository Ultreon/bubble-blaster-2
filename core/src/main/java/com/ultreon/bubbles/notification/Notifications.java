package com.ultreon.bubbles.notification;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Renderable;
import com.ultreon.bubbles.render.Renderer;

import java.util.ArrayDeque;
import java.util.Deque;
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

    public Notifications() {

    }

    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        if (game.isLoading()) return;

        int x = game.getWidth() - NOTIFICATION_OFFSET - NOTIFICATION_WIDTH;
        int y = NOTIFICATION_OFFSET;

        this.lock.lock();
        this.notifications.removeIf(Notification::isDead);
        for (var notification : this.notifications) {
            String title = notification.getTitle();
            String summary = notification.getSummary();
            String subText = notification.getSubText();
            float motionRatio = notification.getMotion();
            float motion = (NOTIFICATION_WIDTH + NOTIFICATION_OFFSET) * motionRatio;

            renderer.setColor(0xff101010);
            renderer.rect(x + motion, y, NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);
            renderer.setColor(0xff505050);
            renderer.rectLine(x + motion + 5, y + 5, NOTIFICATION_WIDTH - 10, NOTIFICATION_HEIGHT - 10);
            renderer.setColor(0xffd0d0d0);
            renderer.drawText(Fonts.SANS_BOLD_15.get(), title, x + motion + 10, y + 13);
            renderer.setColor(0xffb0b0b0);
            renderer.drawText(Fonts.SANS_REGULAR_15.get(), summary, x + motion + 10, y + 40);
            renderer.setColor(0xff707070);
            renderer.drawText(Fonts.SANS_BOLD_ITALIC_10.get(), subText == null ? "" : subText, x + motion + 10, y + 60);

            y += NOTIFICATION_HEIGHT + NOTIFICATION_GAP;
        }
        this.lock.unlock();
    }

    public void notifyPlayer(Notification notification) {
        this.lock.lock();
        this.notifications.addLast(notification);
        this.lock.lock();
    }
}
