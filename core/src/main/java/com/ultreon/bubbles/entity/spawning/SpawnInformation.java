package com.ultreon.bubbles.entity.spawning;

import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.command.Command;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.data.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpawnInformation {
    private final RandomSource random;
    private SpawnReason reason;
    private @Nullable Vector2 pos;
    private MapType tag;
    @Nullable
    private Command command;
    private World world;

    public RandomSource getRandom() {
        return this.random;
    }

    private SpawnInformation(SpawnReason reason, MapType tag, @Nullable Vector2 pos, @Nullable Command command, @NotNull World world, RandomSource random) {
        this.pos = pos;
        this.tag = tag;
        this.reason = reason;
        this.command = command;
        this.world = world;
        this.random = random;
    }

    public static SpawnInformation loadingSpawn(@NotNull MapType data) {
        return new SpawnInformation(SpawnReason.LOAD, data, null, null, BubbleBlaster.getInstance().world, null);
    }

    public static SpawnInformation loadingSpawn(@NotNull MapType data, @NotNull World world) {
        return new SpawnInformation(SpawnReason.LOAD, data, null, null, world, null);
    }

    public static SpawnInformation naturalSpawn(@Nullable Vector2 pos, RandomSource random, SpawnUsage usage, @NotNull World world) {
        return SpawnInformation.naturalSpawn(pos, random, usage, 0, world);
    }

    public static SpawnInformation naturalSpawn(@Nullable Vector2 pos, RandomSource random, SpawnUsage usage, int retry, @NotNull World world) {
        return new SpawnInformation(SpawnReason.natural(usage, retry), new MapType(), pos, null, world, random);
    }

    public static SpawnInformation commandSpawn(Command command) {
        return new SpawnInformation(SpawnReason.COMMAND, new MapType(), null, command, BubbleBlaster.getInstance().world, null);
    }

    public static SpawnInformation commandSpawn(Command command, World world) {
        return new SpawnInformation(SpawnReason.COMMAND, new MapType(), null, command, world, null);
    }

    public static SpawnInformation triggeredSpawn(Player player, World world) {
        return new SpawnInformation(SpawnReason.trigger(player), new MapType(), null, null, world, null);
    }

    public static SpawnInformation playerSpawn(Vector2 pos, World world, RandomSource random) {
        return new SpawnInformation(SpawnReason.PLAYER, new MapType(), pos, null, world, random);
    }

    public SpawnReason getReason() {
        return this.reason;
    }

    public void setReason(SpawnReason reason) {
        this.reason = reason;
    }

    public MapType getData() {
        return this.tag;
    }

    public void setTag(MapType tag) {
        this.tag = tag;
    }

    @Nullable
    public Command getCommand() {
        return this.command;
    }

    public void setCommand(@Nullable Command command) {
        this.command = command;
    }

    @Nullable
    public Vector2 getPos() {
        return this.pos;
    }

    public void setPos(@Nullable Vector2 pos) {
        if (this.pos != null) {
            this.pos = pos;
        }
    }

    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
