package com.ultreon.bubbles.input;

import com.ultreon.bubbles.vector.Vec2i;

import java.awt.*;

public final class MouseInput {
    private final Controller controller = new Controller();
    private static MouseInput instance;

    private MouseInput() {

    }

    public static void init() {
        if (instance == null) {
            instance = new MouseInput();
        }
    }

    public static boolean isPressed(Button button) {
        return instance.controller.isPressed(button.id);
    }

    public static int getX() {
        return getPos().x;
    }

    public static int getY() {
        return getPos().y;
    }

    public static Vec2i getPos() {
        Vec2i mouseVec = instance.controller.getCurrentPoint();
        return mouseVec == null ? new Vec2i(-1, -1) : mouseVec;
    }

    public static void listen(Component canvas) {
        canvas.addMouseListener(instance.controller);
        canvas.addMouseMotionListener(instance.controller);
        canvas.addMouseWheelListener(instance.controller);
    }

    private static class Controller extends com.ultreon.bubbles.core.input.MouseInput {
        @Override
        protected Vec2i getCurrentLocationOnScreen() {
            return super.getCurrentLocationOnScreen();
        }

        @Override
        protected Vec2i getCurrentPoint() {
            return super.getCurrentPoint();
        }

        @Override
        protected int getClickCount() {
            return super.getClickCount();
        }

        @Override
        protected boolean isPressed(int button) {
            return super.isPressed(button);
        }
    }

    enum Button {
        LEFT(1),
        RIGHT(2),
        MIDDLE(3),
        ;

        private final int id;

        Button(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
