package com.ultreon.bubbles.registry.object;

import org.apache.commons.lang3.ObjectUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
//@IndexAnnotated(storeJavadoc = true)
@Deprecated
public @interface ObjectHolder {
    String modId();

    Class<?> type() default ObjectUtils.Null.class;
}
