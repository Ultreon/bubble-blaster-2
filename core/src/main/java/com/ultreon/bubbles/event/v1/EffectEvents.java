package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.libs.events.v1.Event;
import com.ultreon.libs.events.v1.EventResult;

public class EffectEvents {
    public static final Event<Gain> GAIN = Event.withResult();
    public static final Event<Update> UPDATE = Event.withResult();
    public static final Event<Lose> LOSE = Event.create();

    public interface Gain {
        EventResult onGain(AppliedEffect effect);
    }

    public interface Update {
        EventResult onLose(AppliedEffect from, AppliedEffect to);
    }

    public interface Lose {
        void onLose(AppliedEffect effect);
    }
}
