package com.ultreon.bubbles.event.v2;

import com.ultreon.bubbles.effect.AppliedEffect;

public class EffectEvents {
    public static final Event<Gain> GAIN = Event.withResult();
    public static final Event<Update> UPDATE = Event.withResult();
    public static final Event<Lose> LOSE = Event.create();

    public interface Gain {
        EventResult<Boolean> onGain(AppliedEffect effect);
    }

    public interface Update {
        EventResult<AppliedEffect> onLose(AppliedEffect from, AppliedEffect to);
    }

    public interface Lose {
        void onLose(AppliedEffect effect);
    }
}
