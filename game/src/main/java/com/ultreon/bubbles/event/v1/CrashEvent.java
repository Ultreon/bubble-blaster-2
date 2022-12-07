package com.ultreon.bubbles.event.v1;

import com.ultreon.commons.crash.ApplicationCrash;

@Deprecated
public class CrashEvent extends Event {
    private final ApplicationCrash crash;

    public CrashEvent(ApplicationCrash crash) {
        this.crash = crash;
    }

    public ApplicationCrash getCrash() {
        return crash;
    }
}
