package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.command.Command;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.vector.Vec2f;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpawnInformation {
    private SpawnReason reason;
    private @Nullable Vec2f pos;
    private @Nullable CompoundTag tag;
    @Nullable
    private Command command;
    private Environment environment;

    public enum SpawnReason {
        LOAD, NATURAL, COMMAND
    }

    private SpawnInformation(SpawnReason reason, @Nullable CompoundTag tag, @Nullable Vec2f pos, @Nullable Command command, Environment environment) {
        this.pos = pos;
        this.tag = tag;
        this.reason = reason;
        this.command = command;
        this.environment = environment;
    }

    public static SpawnInformation fromLoadSpawn(@NotNull CompoundTag tag) {
        return new SpawnInformation(SpawnReason.LOAD, tag, null, null, BubbleBlaster.getInstance().environment);
    }

    public static SpawnInformation fromNaturalSpawn(Vec2f pos) {
        return new SpawnInformation(SpawnReason.NATURAL, null, pos, null, BubbleBlaster.getInstance().environment);
    }

    public static SpawnInformation fromNaturalSpawn(Vec2f pos, Environment environment) {
        return new SpawnInformation(SpawnReason.NATURAL, null, pos, null, environment);
    }

    public static SpawnInformation fromCommand(Command command) {
        return new SpawnInformation(SpawnReason.COMMAND, null, null, command, BubbleBlaster.getInstance().environment);
    }

    public static SpawnInformation fromCommand(Command command, Environment environment) {
        return new SpawnInformation(SpawnReason.COMMAND, null, null, command, environment);
    }

    public SpawnReason getReason() {
        return reason;
    }

    public void setReason(SpawnReason reason) {
        this.reason = reason;
    }

    public @Nullable CompoundTag getTag() {
        return tag;
    }

    public void setTag(@Nullable CompoundTag tag) {
        if (this.tag != null) {
            this.tag = tag;
        } else {
            throw new NullPointerException("Command property was initialized with null.");
        }
    }

    @Nullable
    public Command getCommand() {
        return command;
    }

    public void setCommand(@Nullable Command command) {
        if (this.tag != null) {
            this.command = command;
        } else {
            throw new NullPointerException("Command property was initialized with null.");
        }
    }

    @Nullable
    public Vec2f getPos() {
        return pos;
    }

    public void setPos(@Nullable Vec2f pos) {
        if (this.pos != null) {
            this.pos = pos;
        } else {
            throw new NullPointerException("Command property was initialized with null.");
        }
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
