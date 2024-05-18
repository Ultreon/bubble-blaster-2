package dev.ultreon.bubbles.common;

import dev.ultreon.bubbles.util.annotation.FieldsAreNonnullByDefault;
import dev.ultreon.bubbles.util.annotation.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Deprecated
public interface Translatable {
    String translationPath();
}
