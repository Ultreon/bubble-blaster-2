package dev.ultreon.bubbles.event.v1;

import dev.ultreon.bubbles.render.gui.screen.Screen;
import dev.ultreon.libs.events.v1.Event;
import dev.ultreon.libs.events.v1.EventResult;
import dev.ultreon.libs.events.v1.ValueEventResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScreenEvents {
    public static final Event<Open> OPEN = Event.withValue();
    public static final Event<ForceOpen> FORCE_OPEN = Event.create();
    public static final Event<Init> INIT = Event.create();
    public static final Event<Close> CLOSE = Event.withResult();
    public static final Event<ForceClose> FORCE_CLOSE = Event.create();

    @FunctionalInterface
    public interface Open {
        ValueEventResult<@Nullable Screen> onOpen(@Nullable Screen screen);
    }

    @FunctionalInterface
    public interface ForceOpen {
        void onOpen(@Nullable Screen screen);
    }

    @FunctionalInterface
    public interface Init {
        void onInit(Screen screen);
    }

    @FunctionalInterface
    public interface Close {
        EventResult onClose(@NotNull Screen screen);
    }

    @FunctionalInterface
    public interface ForceClose {
        void onForceClose(@NotNull Screen screen);
    }
}
