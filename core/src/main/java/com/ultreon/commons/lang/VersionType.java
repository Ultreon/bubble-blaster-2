package com.ultreon.commons.lang;

import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Deprecated
public enum VersionType {
    ALPHA("alpha"), BETA("beta"), RELEASE("release"), CANDIDATE("rc");

    private final String name;

    VersionType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String toRepresentation() {
        return "VersionType{" +
                "name='" + this.name + '\'' +
                '}';
    }

    public String getName() {
        return this.name;
    }
}
