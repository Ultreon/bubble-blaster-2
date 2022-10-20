package com.ultreon.bubbles.event.v1.entity;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.event.v1.Event;

@Deprecated
public abstract class StatusEffectEvent extends Event {
    protected AppliedEffect statusEffect;

    public StatusEffectEvent(AppliedEffect statusEffect) {
        this.statusEffect = statusEffect;
    }

    public static class Gain extends StatusEffectEvent {
        public Gain(AppliedEffect statusEffect) {
            super(statusEffect);
        }

        public void setStatusEffect(AppliedEffect statusEffect) {
            this.statusEffect = statusEffect;
        }
    }

    public static class Update extends StatusEffectEvent {
        private final AppliedEffect from;

        public Update(AppliedEffect from, AppliedEffect statusEffect) {
            super(statusEffect);
            this.from = from;
        }

        public AppliedEffect getTo() {
            return statusEffect;
        }

        public void setTo(AppliedEffect statusEffect) {
            this.statusEffect = statusEffect;
        }

        public AppliedEffect getFrom() {
            return from;
        }
    }

    public static class Loose extends StatusEffectEvent {
        public Loose(AppliedEffect statusEffect) {
            super(statusEffect);
        }
    }
}
