package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.common.TagHolder;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.util.helpers.MathHelper;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.exceptions.InvalidValueException;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.Objects;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StatusEffectInstance implements TagHolder {
    private final StatusEffect type;
    private int strength;
    private long endTime;

    private boolean active;
    private MapType tag = new MapType();
    private long baseDuration;

    /**
     * @throws ClassCastException when effect couldn't be recognized.
     */
    private StatusEffectInstance(MapType document) {
        this.tag = document.getMap("Tag");
        Identifier id = Identifier.tryParse(document.getString("id"));
        if (id == null) {
            this.type = StatusEffects.NONE;
        } else {
            var type = Registries.EFFECTS.getValue(id);
            this.type = type == null ? StatusEffects.NONE : type;
        }
        this.setRemainingTime(Duration.ofMillis(document.getLong("duration")));
        this.baseDuration = document.getLong("baseDuration");
        this.strength = document.getInt("strength");
    }

    public StatusEffectInstance(StatusEffect type, double seconds, int strength) throws InvalidValueException {
        this(type, Duration.ofMillis((long) (seconds / 1000f)), strength);
    }

    public StatusEffectInstance(StatusEffect type, Duration duration, int strength) throws InvalidValueException {
        if (strength < 1) {
            throw new InvalidValueException("Cannot create effect instance with strength < 1");
        }

        this.type = type;
        this.strength = strength;
        this.setRemainingTime(duration);
    }

    public static @NotNull StatusEffectInstance load(MapType activeEffectData) {
        return new StatusEffectInstance(activeEffectData);
    }

    public @NotNull MapType save() {
        MapType tag = new MapType();
        tag.put("Tag", this.tag);
        tag.putLong("baseDuration", this.getBaseDuration());
        tag.putLong("duration", this.getRemainingTime().toMillis());
        tag.putInt("strength", this.getStrength());

        Identifier key = Registries.EFFECTS.getKey(this.getType());
        tag.putString("id", key == null ? "none" : key.toString());

        return tag;
    }

    public boolean allowMerge() {
        return true;
    }

    public final void start(@NotNull Entity entity) {
        this.onStart(entity);

        this.active = true;
    }

    public final void stop(@NotNull Entity entity) {
        this.onStop(entity);

        this.active = false;
    }

    public final StatusEffect getType() {
        return this.type;
    }

    public void tick(Entity entity) {
        if (this.getRemainingTime().isZero() || this.getRemainingTime().isNegative()) {
            this.active = false;
            this.stop(entity);
            entity.removeEffect(this);
        } else {
            this.type.tick(entity, this);
        }
    }

    public void onStart(Entity entity) {
        this.type.onStart(this, entity);
    }

    public void onStop(Entity entity) {
        this.type.onStop(entity);
    }

    @SuppressWarnings("EmptyMethod")
    protected void updateStrength(float old, float _new) {

    }

    public void addStrength() {
        float old = this.getStrength();
        byte output = (byte) (this.strength + 1);
        this.strength = MathHelper.clamp(output, 1, 255);
        this.updateStrength(old, this.getStrength());
    }

    public void addStrength(byte amount) {
        float old = this.getStrength();
        byte output = (byte) (this.strength + amount);
        this.strength = MathHelper.clamp(output, 1, 255);
        this.updateStrength(old, this.getStrength());
    }

    public void removeStrength() {
        float old = this.getStrength();
        byte output = (byte) (this.strength - 1);
        this.strength = MathHelper.clamp(output, 1, 255);
        this.updateStrength(old, this.getStrength());
    }

    public void removeStrength(byte amount) {
        float old = this.getStrength();
        byte output = (byte) (this.strength - amount);
        this.strength = MathHelper.clamp(output, 1, 255);
        this.updateStrength(old, this.getStrength());
    }

    public final int getStrength() {
        return this.strength;
    }

    public void setStrength(int strength) throws InvalidValueException {
        float old = this.getStrength();
        if (strength < 1) {
            throw new InvalidValueException("Tried to set strength less than 1.");
        }

        this.strength = strength;
        this.updateStrength(old, this.getStrength());
    }

    public final long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Duration getRemainingTime() {
        return Duration.ofMillis(this.endTime - System.currentTimeMillis());
    }

    public void setRemainingTime(Duration time) {
        this.endTime = System.currentTimeMillis() + time.toMillis();
    }

    public void prolong(Duration time) {
        this.setRemainingTime(this.getRemainingTime().plus(time));
    }

    public void elapse(Duration time) {
        this.setRemainingTime(this.getRemainingTime().minus(time));
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        StatusEffectInstance appliedEffect = (StatusEffectInstance) o;
        return Objects.equals(this.getType(), appliedEffect.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getType());
    }

    @Override
    public MapType getTag() {
        return this.tag;
    }

    public long getStartTime() {
        return this.getEndTime() - this.baseDuration;
    }

    public long getTimeActive() {
        return System.currentTimeMillis() - this.getStartTime();
    }

    public long getBaseDuration() {
        return this.baseDuration;
    }
}
