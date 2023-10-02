package com.ultreon.bubbles.entity.ai;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.types.EntityType;

public class AiTarget extends AiTask {
    private final EntityType<?> targetType;

    public AiTarget(EntityType<?> targetType) {
        this.targetType = targetType;
    }

    @Override
    public boolean executeTask(Entity entity) {
        Entity nearestEntity = entity.getWorld().getNearestEntity(entity.getPos(), targetType);
        Entity target = entity.getTarget();
        if (target == null) {
            if (nearestEntity != null) entity.setTarget(nearestEntity);
            return true;
        }
        return false;
    }
}
