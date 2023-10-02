package com.ultreon.bubbles.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.*;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.event.v1.ConfigEvents;
import com.ultreon.bubbles.notification.Notification;
import org.apache.logging.log4j.core.util.FileWatcher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Config {
    private static final TomlParser PARSER = TomlFormat.instance().createParser();
    private static final TomlWriter WRITER = TomlFormat.instance().createWriter();
    static {
        WRITER.setNewline(NewlineStyle.system());
        WRITER.setIndent(IndentStyle.SPACES_2);
    }
    private final File file;
    private final CommentedConfig config;
    private final Map<String, ConfigEntry<?>> entries;
    private final FileWatcher watcher;


    public Config(Builder builder) {
        this.file = builder.file;
        this.config = builder.config;
        this.entries = builder.entries;
        this.entries.forEach((s, configEntry) -> configEntry.set0(configEntry.getDefaultValue()));
        this.watcher = this::fileModified;

        this.watch();
    }

    private void watch() {
        BubbleBlaster.getWatcher().watchFile(this.file, this.watcher);
    }

    private void unwatch() {
        BubbleBlaster.getWatcher().unwatchFile(this.file);
    }

    public synchronized void reload() {
        this.unwatch();
        PARSER.parse(this.file, this.config, ParsingMode.REPLACE, FileNotFoundAction.CREATE_EMPTY);
        this.entries.forEach((s, configEntry) -> {
            if (configEntry.isSet()) return;
            configEntry.set0(configEntry.getDefaultValue());
        });

        ConfigEvents.CONFIG_RELOADED.factory().onConfigReloaded(this);
        this.watch();
    }

    public synchronized void save() {
        this.unwatch();
        if (ConfigEvents.CONFIG_SAVING.factory().onConfigSaving(this).isCanceled()) return;

        BubbleBlaster game = BubbleBlaster.getInstance();

        try {
            WRITER.write(this.config, this.file, WritingMode.REPLACE);

            if (WRITER.writeToString(this.config).isBlank()) {
                game.notifications.notify(
                        Notification.builder("Config Failed to Save!", "Failed to save config '" + this.file.getName() + "'")
                                .subText("Config Manager")
                                .build()
                );
            }
        } catch (Exception e) {
            game.notifications.notify(
                    Notification.builder("Config Failed to Save!", "Failed to save config '" + this.file.getName() + "'")
                            .subText("Config Manager")
                            .build());
        }

        ConfigEvents.CONFIG_SAVED.factory().onConfigSaved(this);
        this.watch();
    }

    public CommentedConfig getConfig() {
        return this.config;
    }

    public File getFile() {
        return this.file;
    }

    public Set<Entry<String, ConfigEntry<?>>> entrySet() {
        return this.entries.entrySet();
    }

    private void fileModified(File f) {
        if (f.equals(this.file)) {
            try {
                this.reload();
            } catch (ParsingException ignored) {

            }
        }
    }

    public static class EnumEntry<T extends Enum<T>> extends ConfigEntry<T> {
        public EnumEntry(CommentedConfig config, String path, T defaultValue) {
            super(config, path, defaultValue);

            Enum<T> n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            }
        }

        @Override
        public T get() {
            return this.config.getEnum(this.path, this.getDefaultValue().getDeclaringClass());
        }
    }

    public static class StringEntry extends ConfigEntry<String> {
        public StringEntry(CommentedConfig config, String path, String defaultValue) {
            super(config, path, defaultValue);

            String n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            }
        }
    }

    public static class BooleanEntry extends ConfigEntry<Boolean> {
        public BooleanEntry(CommentedConfig config, String path, boolean defaultValue) {
            super(config, path, defaultValue);

            Boolean n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            }
        }
    }

    public static class CharEntry extends ConfigEntry<Character> {
        public CharEntry(CommentedConfig config, String path, char defaultValue) {
            super(config, path, defaultValue);

            Character n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            }
        }
    }

    public static class ByteEntry extends ConfigEntry<Byte> {
        public ByteEntry(CommentedConfig config, String path, byte minValue, byte maxValue, byte defaultValue) {
            super(config, path, defaultValue);

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.byteValue() < minValue) {
                config.set(path, minValue);
            } else if (n.byteValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        @Override
        public Byte get() {
            return this.config.<Number>get(this.path).byteValue();
        }
    }

    public static class ShortEntry extends ConfigEntry<Short> {
        public ShortEntry(CommentedConfig config, String path, short minValue, short maxValue, short defaultValue) {
            super(config, path, defaultValue);

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.shortValue() < minValue) {
                config.set(path, minValue);
            } else if (n.shortValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        @Override
        public Short get() {
            return this.config.<Number>get(this.path).shortValue();
        }
    }

    public static class IntEntry extends ConfigEntry<Integer> {
        public IntEntry(CommentedConfig config, String path, int minValue, int maxValue, int defaultValue) {
            super(config, path, defaultValue);

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.intValue() < minValue) {
                config.set(path, minValue);
            } else if (n.intValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        @Override
        public Integer get() {
            return this.config.<Number>get(this.path).intValue();
        }
    }

    public static class LongEntry extends ConfigEntry<Long> {
        public LongEntry(CommentedConfig config, String path, long minValue, long maxValue, long defaultValue) {
            super(config, path, defaultValue);

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.longValue() < minValue) {
                config.set(path, minValue);
            } else if (n.longValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        @Override
        public Long get() {
            return this.config.<Number>get(this.path).longValue();
        }
    }

    public static class FloatEntry extends ConfigEntry<Float> {
        public FloatEntry(CommentedConfig config, String path, float minValue, float maxValue, float defaultValue) {
            super(config, path, defaultValue);

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.floatValue() < minValue) {
                config.set(path, minValue);
            } else if (n.floatValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        @Override
        public Float get() {
            Number number = this.config.get(this.path);
            if (number == null) return this.getDefaultValue();
            return number.floatValue();
        }
    }

    public static class DoubleEntry extends ConfigEntry<Double> {
        public DoubleEntry(CommentedConfig config, String path, double minValue, double maxValue, double defaultValue) {
            super(config, path, defaultValue);

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.doubleValue() < minValue) {
                config.set(path, minValue);
            } else if (n.doubleValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        @Override
        public Double get() {
            return this.config.<Number>get(this.path).doubleValue();
        }
    }

    public static class ConfigEntry<T> {
        protected final CommentedConfig config;
        protected final String path;
        private final T defaultValue;

        public ConfigEntry(CommentedConfig config, String path, T defaultValue) {
            this.config = config;
            this.path = path;
            this.defaultValue = defaultValue;
        }

        public T get() {
            T value = this.config.get(this.path);
            if (value == null) return this.defaultValue;
            return this.config.get(this.path);
        }

        public boolean isSet() {
            return this.config.contains(this.path);
        }

        public void set(T value) {
            this.config.set(this.path, value);
        }

        void set0(Object value) {
            this.config.set(this.path, value);
        }

        public String getComment() {
            return this.config.getComment(this.path);
        }

        public void setComment(String comment) {
            this.config.setComment(this.path, comment);
        }

        public T getDefaultValue() {
            return this.defaultValue;
        }

        public T getOrDefault() {
            return this.get();
        }
    }

    public static class Builder {
        private final Map<String, ConfigEntry<?>> entries = new HashMap<>();
        private final CommentedConfig config;
        private final File file;

        public Builder(File file) {
            this.file = file;
            this.config = TomlFormat.instance().createConcurrentConfig();
        }

        public EntryBuilder entry(String path) {
            return new EntryBuilder(this, path);
        }

        public Config build() {
            return new Config(this);
        }

        public static class EntryBuilder {
            private final Builder builder;
            private final String path;
            private String comment;

            public EntryBuilder(Builder builder, String path) {
                this.path = path;
                this.builder = builder;
            }

            public EntryBuilder comment(String comment) {
                this.comment = comment;
                return this;
            }

            public ByteEntry withinRange(byte min, byte max, byte def) {
                ByteEntry entry = new ByteEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public ShortEntry withinRange(short min, short max, short def) {
                ShortEntry entry = new ShortEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public IntEntry withinRange(int min, int max, int def) {
                IntEntry entry = new IntEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public LongEntry withinRange(long min, long max, long def) {
                LongEntry longEntry = new LongEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, longEntry);
                return longEntry;
            }

            public FloatEntry withinRange(float min, float max, float def) {
                FloatEntry entry = new FloatEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public DoubleEntry withinRange(double min, double max, double def) {
                DoubleEntry entry = new DoubleEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public CharEntry value(char def) {
                CharEntry entry = new CharEntry(this.builder.config, this.path, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public BooleanEntry value(boolean def) {
                BooleanEntry entry = new BooleanEntry(this.builder.config, this.path, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public StringEntry value(String def) {
                StringEntry entry = new StringEntry(this.builder.config, this.path, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public <T extends Enum<T>> EnumEntry<T> value(T def) {
                EnumEntry<T> entry = new EnumEntry<>(this.builder.config, this.path, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }
        }
    }
}
