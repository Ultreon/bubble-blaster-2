package com.ultreon.bubbles.common.interfaces;

import com.ultreon.data.types.MapType;
import org.jetbrains.annotations.NotNull;

/**
 * Holder for a state that can be saved/loaded.
 * @since 0.0.0
 * @author XyperCode
 */
public interface StateHolder {
    /**
     * Save the state.
     *
     * @return the saved state.
     * @author XyperCode
     * @since 0.0.0
     */
    @NotNull
    MapType save();

    /**
     * Load a previously saved state.
     * @param tag the state data.
     * @since 0.0.0
     * @author XyperCode
     */
    void load(MapType tag);
}
