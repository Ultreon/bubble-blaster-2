package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.config.Config;
import com.ultreon.libs.events.v1.Event;
import com.ultreon.libs.events.v1.EventResult;

public class ConfigEvents {
    public static final Event<ConfigReloaded> CONFIG_RELOADED = Event.create();
    public static final Event<ReloadAll> RELOAD_ALL = Event.create();
    public static final Event<ConfigSaved> CONFIG_SAVED = Event.create();
    public static final Event<ConfigSaving> CONFIG_SAVING = Event.withResult();

    public interface ConfigReloaded {
        void onConfigReloaded(Config spec);
    }

    public interface ReloadAll {
        void onReloadAll();
    }

    public interface ConfigSaving {
        EventResult onConfigSaving(Config spec);
    }

    public interface ConfigSaved {
        void onConfigSaved(Config spec);
    }
}
