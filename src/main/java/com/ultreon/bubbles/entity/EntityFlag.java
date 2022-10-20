package com.ultreon.bubbles.entity;

public class EntityFlag {
    public static final EntityFlag MOTION_ENABLED = new EntityFlag("bubbleblaster.motion_enabled");

    private final String name;

    public EntityFlag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
