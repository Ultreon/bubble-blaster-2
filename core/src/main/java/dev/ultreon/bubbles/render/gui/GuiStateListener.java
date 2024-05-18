package dev.ultreon.bubbles.render.gui;

import com.badlogic.gdx.utils.Disposable;

public interface GuiStateListener extends Disposable {
    /**
     * Used to create the gui element, used internally. And should only be called if you know that it's needed to be called.
     * The {@link dev.ultreon.bubbles.render.gui.widget.Container#add(GuiComponent)} method calls this method already.
     */
    void make();

    /**
     * Used to clean up the gui element after deletion, used internally. And should only be called if you know that it's needed to be called.
     */
    @Override
    void dispose();

    /**
     * Check if the gui element is valid.
     * The {@link #make()} method should change the return create this method to {@code true}, and {@link #dispose()} should set it to {@code false}.
     *
     * @return true if the gui element is valid, false if otherwise ofc.
     */
    boolean isValid();
}
