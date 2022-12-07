package com.ultreon.bubbles.api.event;

import com.ultreon.bubbles.api.event.keyboard.KeyboardModifiers;
import com.ultreon.bubbles.event.v1.type.KeyEventType;

@Deprecated(since = "0.0.3047-indev5")
public class KeyboardEvent extends Event<KeyboardEvent.KeyboardEventListener> {
    @Deprecated(since = "0.0.3047-indev5")
    public KeyboardEvent() {
        super(KeyboardEventListener.class);
    }

    @Deprecated(since = "0.0.3047-indev5")
    public interface KeyboardEventListener extends IListener {
        @Deprecated(since = "0.0.3047-indev5")
        void onKeyboard(KeyEventType type, char key, int keyCode, KeyboardModifiers modifiers);
    }
}
