package com.ultreon.bubbles.common.interfaces;

import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Holder for a state that can be saved/loaded.
 * @since 0.0.0
 * @author Qboi123
 */
public interface StateHolder {
    /**
     * Save the state.
     * @return the saved state.
     * @since 0.0.0
     * @author Qboi123
     */
    @NotNull
    CompoundTag save();

    /**
     * Load a previously saved state.
     * @param tag the state data.
     * @since 0.0.0
     * @author Qboi123
     */
    void load(CompoundTag tag);
}
