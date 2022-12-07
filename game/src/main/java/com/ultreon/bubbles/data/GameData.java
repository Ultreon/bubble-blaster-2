package com.ultreon.bubbles.data;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

import java.io.File;
import java.io.IOException;

public abstract class GameData {
    protected abstract CompoundTag dump(CompoundTag tag);

    protected final void dump(File file) throws IOException {
        NBTUtil.write(dump(new CompoundTag()), file, true);
    }

    protected abstract void load(CompoundTag tag);

    protected final void load(File file) throws IOException {
        load((CompoundTag) NBTUtil.read(file, true).getTag());
    }
}
