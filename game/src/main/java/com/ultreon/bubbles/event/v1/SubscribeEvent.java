package com.ultreon.bubbles.event.v1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent {
    @Deprecated
    boolean ignoreCancelled() default false;

    @Deprecated
    EventPriority priority() default EventPriority.NORMAL;
}
