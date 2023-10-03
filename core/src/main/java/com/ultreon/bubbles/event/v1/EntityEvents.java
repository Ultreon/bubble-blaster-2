package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.damage.DamageSource;
import com.ultreon.bubbles.entity.spawning.SpawnInformation;
import com.ultreon.libs.events.v1.Event;
import com.ultreon.libs.events.v1.EventResult;
import com.ultreon.libs.events.v1.ValueEventResult;

public class EntityEvents {
    public static final Event<Spawn> SPAWN = Event.withResult();
    public static final Event<Collision> COLLISION = Event.create();
    public static final Event<Damage> DAMAGE = Event.withValue();

    @FunctionalInterface
    public interface Spawn {
        EventResult onSpawn(Entity entity, SpawnInformation information);
    }

    @FunctionalInterface
    public interface Collision {
        void onCollision(double delta, Entity entityA, Entity entityB);
    }

    @FunctionalInterface
    public interface Damage {
        ValueEventResult<Double> onDamage(Entity entity, DamageSource source, double amount);
    }
}
