package com.ultreon.bubbles.event.v1.entity;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.damage.DamageSource;
import com.ultreon.commons.lang.ICancellable;

@Deprecated
public class EntityDamageEvent extends EntityEvent implements ICancellable {
    private DamageSource damageSource;

    public EntityDamageEvent(Entity entity, DamageSource damageSource) {
        super(entity);
        this.damageSource = damageSource;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public void setDamageSource(DamageSource damageSource) {
        this.damageSource = damageSource;
    }
}
