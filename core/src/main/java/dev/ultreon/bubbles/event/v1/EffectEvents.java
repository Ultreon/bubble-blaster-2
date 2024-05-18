package dev.ultreon.bubbles.event.v1;

import dev.ultreon.bubbles.effect.StatusEffectInstance;
import dev.ultreon.libs.events.v1.Event;
import dev.ultreon.libs.events.v1.EventResult;

public class EffectEvents {
    public static final Event<Gain> GAIN = Event.withResult();
    public static final Event<Update> UPDATE = Event.withResult();
    public static final Event<Lose> LOSE = Event.withResult();

    public interface Gain {
        EventResult onGain(StatusEffectInstance effect);
    }

    public interface Update {
        EventResult onUpdate(StatusEffectInstance from);
    }

    public interface Lose {
        EventResult onLose(StatusEffectInstance effect);
    }
}
