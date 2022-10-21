package com.ultreon.bubbles.mod.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@AntiMod
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AntiMod {

}
