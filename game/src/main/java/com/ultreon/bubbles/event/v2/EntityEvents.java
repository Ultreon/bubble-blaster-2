package com.ultreon.bubbles.event.v2;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.SpawnInformation;
import com.ultreon.bubbles.entity.damage.DamageSource;

public class EntityEvents {
    public static final Event<Spawn> SPAWN = Event.create();
    public static final Event<Collision> COLLISION = Event.create();
    public static final Event<Damage> DAMAGE = Event.withResult();

    @FunctionalInterface
    public interface Spawn {
        void onSpawn(Entity entity, SpawnInformation information);
    }

    @FunctionalInterface
    public interface Collision {
        void onCollision(double delta, Entity entityA, Entity entityB);
    }

    @FunctionalInterface
    public interface Damage {
        EventResult<Double> onDamage(Entity entity, DamageSource source, double amount);
    }
}
