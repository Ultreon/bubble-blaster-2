package com.ultreon.bubbles.common.interfaces;

import com.ultreon.data.types.MapType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Holder for a state that can be saved/loaded.
 * @since 0.0.0
 * @author Qboi123
 */
public interface StateHolder {
    /**
     * Save the state.
     *
     * @return the saved state.
     * @author Qboi123
     * @since 0.0.0
     */
    @NonNull
    MapType save();

    /**
     * Load a previously saved state.
     * @param tag the state data.
     * @since 0.0.0
     * @author Qboi123
     */
    void load(MapType tag);
}
