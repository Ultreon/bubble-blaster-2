package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.common.TagHolder;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.init.StatusEffects;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.exceptions.InvalidValueException;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AppliedEffect implements TagHolder {
    private final StatusEffect type;
    private int strength;
    private long endTime;

    private boolean active;
    private MapType tag = new MapType();
    private long baseDuration;

    /**
     * @throws ClassCastException when effect couldn't be recognized.
     */
    private AppliedEffect(MapType document) {
        this.tag = document.getMap("Tag");
        Identifier id = Identifier.tryParse(document.getString("id"));
        if (id == null) {
            this.type = StatusEffects.NONE;
        } else {
            var type = Registries.EFFECTS.getValue(id);
            this.type = type == null ? StatusEffects.NONE : type;
        }
        this.setRemainingTime(document.getLong("duration"));
        this.baseDuration = document.getLong("baseDuration");
        this.strength = document.getInt("strength");
    }

    public AppliedEffect(StatusEffect type, long duration, @IntRange(from = 1, to = 255) int strength) throws InvalidValueException {
        //noinspection ConstantConditions
        if (strength < 1) {
            throw new InvalidValueException("Cannot create effect instance with strength < 1");
        }

        this.type = type;
        this.strength = strength;
        this.setRemainingTime(duration);
    }

    public static @NotNull AppliedEffect load(MapType activeEffectData) {
        return new AppliedEffect(activeEffectData);
    }

    public @NotNull MapType save() {
        MapType tag = new MapType();
        tag.put("Tag", this.tag);
        tag.putLong("baseDuration", getBaseDuration());
        tag.putLong("duration", getRemainingTime());
        tag.putInt("strength", getStrength());

        Identifier key = Registries.EFFECTS.getKey(getType());
        tag.putString("id", key == null ? "none" : key.toString());

        return tag;
    }

    public boolean allowMerge() {
        return true;
    }

    public final void start(@NotNull Entity entity) {
        onStart(entity);

        active = true;
    }

    public final void stop(@NotNull Entity entity) {
        onStop(entity);

        active = false;
    }

    public final StatusEffect getType() {
        return type;
    }

    public void tick(Entity entity) {
        System.out.println("getRemainingTime() = " + getRemainingTime());
        if (this.getRemainingTime() <= 0d) {
            this.active = false;
            this.stop(entity);
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
    protected void updateStrength(int old, int _new) {

    }

    public void addStrength() {
        int old = getStrength();
        byte output = (byte) (this.strength + 1);
        this.strength = Mth.clamp(output, 1, 255);
        updateStrength(old, getStrength());
    }

    public void addStrength(byte amount) {
        int old = getStrength();
        byte output = (byte) (this.strength + amount);
        this.strength = Mth.clamp(output, 1, 255);
        updateStrength(old, getStrength());
    }

    public void removeStrength() {
        int old = getStrength();
        byte output = (byte) (this.strength - 1);
        this.strength = Mth.clamp(output, 1, 255);
        updateStrength(old, getStrength());
    }

    public void removeStrength(byte amount) {
        int old = getStrength();
        byte output = (byte) (this.strength - amount);
        this.strength = Mth.clamp(output, 1, 255);
        updateStrength(old, getStrength());
    }

    public final int getStrength() {
        return this.strength;
    }

    public void setStrength(byte strength) throws InvalidValueException {
        int old = getStrength();
        if (strength < 1) {
            throw new InvalidValueException("Tried to set strength less than 1.");
        }

        this.strength = strength;
        updateStrength(old, getStrength());
    }

    public final long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getRemainingTime() {
        return (this.endTime - System.currentTimeMillis()) / 1000;
    }

    public void setRemainingTime(long time) {
        this.endTime = System.currentTimeMillis() + time * 1000;
    }

    public void addTime(long time) {
        this.setRemainingTime(this.getRemainingTime() + time);
    }

    public void removeTime(long time) {
        this.setRemainingTime(this.getRemainingTime() - time);
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        AppliedEffect appliedEffect = (AppliedEffect) o;
        return Objects.equals(this.getType(), appliedEffect.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType());
    }

    @Override
    public MapType getTag() {
        return this.tag;
    }

    public long getStartTime() {
        return this.getEndTime() - this.baseDuration;
    }

    public long getBaseDuration() {
        return this.baseDuration;
    }
}
