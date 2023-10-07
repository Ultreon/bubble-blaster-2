package com.ultreon.bubbles.entity.ai;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.LivingEntity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.DamageType;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;

public class AiAttack extends AiTask {
    @Override
    public boolean executeTask(Entity entity) {
        var target = entity.getTarget();
        if (target instanceof LivingEntity && entity.isCollidingWith(target)) {
            ((LivingEntity) target).damage(entity.getAttributes().get(Attribute.ATTACK), new EntityDamageSource(entity, DamageType.ATTACK));
            return true;
        }
        return false;
    }
}
