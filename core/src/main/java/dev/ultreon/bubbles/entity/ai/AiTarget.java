package dev.ultreon.bubbles.entity.ai;

import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.types.EntityType;

public class AiTarget extends AiTask {
    private final EntityType<?> targetType;

    public AiTarget(EntityType<?> targetType) {
        this.targetType = targetType;
    }

    @Override
    public boolean executeTask(Entity entity) {
        var nearestEntity = entity.getWorld().getNearestEntity(entity.getPos(), this.targetType);
        var target = entity.getTarget();
        if (target == null) {
            if (nearestEntity != null) entity.setTarget(nearestEntity);
            return true;
        }
        return false;
    }
}
