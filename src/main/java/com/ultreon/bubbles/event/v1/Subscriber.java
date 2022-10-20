package com.ultreon.bubbles.event.v1;

@Deprecated
public abstract class Subscriber<T extends AbstractEvent> {
    @Deprecated
    public abstract void handle(T e);

    @Deprecated
    public abstract EventPriority getPriority();

    @Deprecated
    public abstract SubscribeEvent getAnnotation();

    @Deprecated
    public abstract Class<? extends Event> getType();
}
