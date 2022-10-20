package com.ultreon.bubbles.common.interfaces;

import com.ultreon.bubbles.event.v1.SubscribeEvent;

@FunctionalInterface
public interface EventConsumer<T> {
    @SubscribeEvent
    void accept(T evt);
}
