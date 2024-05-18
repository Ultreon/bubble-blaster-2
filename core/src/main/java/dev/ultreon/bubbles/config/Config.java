package dev.ultreon.bubbles.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.*;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.event.v1.ConfigEvents;
import dev.ultreon.bubbles.notification.Notification;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Config {
    private static final TomlParser PARSER = TomlFormat.instance().createParser();
    private static final TomlWriter WRITER = TomlFormat.instance().createWriter();
    public static String hexagonColor = "#ffffff";
    public static float hexagonTransparency = 0.2f;
    public static float blurRadius = 14f;

    static {
        WRITER.setNewline(NewlineStyle.system());
        WRITER.setIndent(IndentStyle.SPACES_2);
    }
    private final File file;
    private final CommentedConfig config;
    private final Map<String, ConfigEntry<?>> entries;


    public Config(Builder builder) {
        this.file = builder.file;
        this.config = builder.config;
        this.entries = builder.entries;
        this.entries.values().forEach(ConfigEntry::reset);
        this.watch();
    }

    private void watch() {
        // TODO implement file watching!
    }

    private void unwatch() {
        // TODO implement file watching!
    }

    public synchronized void reload() {
        this.unwatch();
        try {
            PARSER.parse(this.file, this.config, ParsingMode.REPLACE, FileNotFoundAction.READ_NOTHING);
            this.entries.forEach((s, configEntry) -> {
                var o = this.config.get(configEntry.path);
                if (o != null && configEntry.setSafe(o))
                    return;

                configEntry.reset();
            });
        } catch (ParsingException e) {
            var fileName = this.file.getName();
            BubbleBlaster.LOGGER.error("Failed to load config '" + fileName + "'", e);
            BubbleBlaster.getInstance().notifications.notify(
                    Notification.builder("Config Failed to Load!", "Failed to load '" + fileName + "'")
                            .subText("Configuration Manager")
                            .build()
            );
            var backupFile = new File(this.file.getParentFile(), fileName + ".bak");
            if (backupFile.exists()) {
                BubbleBlaster.LOGGER.warn("Backup of config '" + fileName + "' already exists!");
            }

            try {
                Files.copy(this.file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            this.reset();
            this.save();
        }
        ConfigEvents.CONFIG_RELOADED.factory().onConfigReloaded(this);
        this.watch();
    }

    public synchronized void reset() {
        this.unwatch();
        this.entries.forEach((s, configEntry) -> {
            configEntry.reset();
            configEntry.setComment(configEntry.getComment());
        });

        ConfigEvents.CONFIG_RELOADED.factory().onConfigReloaded(this);
        this.watch();
    }

    public synchronized void save() {
        this.unwatch();
        if (ConfigEvents.CONFIG_SAVING.factory().onConfigSaving(this).isCanceled()) return;

        var game = BubbleBlaster.getInstance();

        try {
            WRITER.write(this.config, this.file, WritingMode.REPLACE);
            if (WRITER.writeToString(this.config).isBlank()) {
                BubbleBlaster.LOGGER.error("Failed to save config, the config was empty.");
                game.notifications.notify(
                        Notification.builder("Config Failed to Save!", "Failed to save config '" + this.file.getName() + "'")
                                .subText("Configuration Manager")
                                .build()
                );
            }
        } catch (Exception e) {
            BubbleBlaster.LOGGER.error("Failed to save config: ", e);
            game.notifications.notify(
                    Notification.builder("Config Failed to Save!", "Failed to save config '" + this.file.getName() + "'")
                            .subText("Configuration Manager")
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
        private final byte minValue;
        private final byte maxValue;

        public ByteEntry(CommentedConfig config, String path, byte minValue, byte maxValue, byte defaultValue) {
            super(config, path, defaultValue);
            this.minValue = minValue;
            this.maxValue = maxValue;

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.byteValue() < minValue) {
                config.set(path, minValue);
            } else if (n.byteValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        public byte getMinValue() {
            return this.minValue;
        }

        public byte getMaxValue() {
            return this.maxValue;
        }

        @Override
        protected Byte get0() {
            Number number = this.config.get(this.path);
            if (number == null) return this.getDefaultValue();
            return number.byteValue();
        }
    }

    public static class ShortEntry extends ConfigEntry<Short> {
        private final short minValue;
        private final short maxValue;

        public ShortEntry(CommentedConfig config, String path, short minValue, short maxValue, short defaultValue) {
            super(config, path, defaultValue);
            this.minValue = minValue;
            this.maxValue = maxValue;

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.shortValue() < minValue) {
                config.set(path, minValue);
            } else if (n.shortValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        public short getMinValue() {
            return this.minValue;
        }

        public short getMaxValue() {
            return this.maxValue;
        }

        @Override
        protected Short get0() {
            Number number = this.config.get(this.path);
            if (number == null) return this.getDefaultValue();
            return number.shortValue();
        }
    }

    public static class IntEntry extends ConfigEntry<Integer> {
        private final int minValue;
        private final int maxValue;

        public IntEntry(CommentedConfig config, String path, int minValue, int maxValue, int defaultValue) {
            super(config, path, defaultValue);
            this.minValue = minValue;
            this.maxValue = maxValue;

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.intValue() < minValue) {
                config.set(path, minValue);
            } else if (n.intValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        public int getMinValue() {
            return this.minValue;
        }

        public int getMaxValue() {
            return this.maxValue;
        }

        @Override
        protected Integer get0() {
            Number number = this.config.get(this.path);
            if (number == null) return this.getDefaultValue();
            return number.intValue();
        }
    }

    public static class LongEntry extends ConfigEntry<Long> {
        private final long minValue;
        private final long maxValue;

        public LongEntry(CommentedConfig config, String path, long minValue, long maxValue, long defaultValue) {
            super(config, path, defaultValue);
            this.minValue = minValue;
            this.maxValue = maxValue;

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.longValue() < minValue) {
                config.set(path, minValue);
            } else if (n.longValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        public long getMinValue() {
            return this.minValue;
        }

        public long getMaxValue() {
            return this.maxValue;
        }

        @Override
        protected Long get0() {
            Number number = this.config.get(this.path);
            if (number == null) return this.getDefaultValue();
            return number.longValue();
        }
    }

    public static class FloatEntry extends ConfigEntry<Float> {
        private final float minValue;
        private final float maxValue;

        public FloatEntry(CommentedConfig config, String path, float minValue, float maxValue, float defaultValue) {
            super(config, path, defaultValue);
            this.minValue = minValue;
            this.maxValue = maxValue;

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.floatValue() < minValue) {
                config.set(path, minValue);
            } else if (n.floatValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        public float getMinValue() {
            return this.minValue;
        }

        public float getMaxValue() {
            return this.maxValue;
        }

        @Override
        protected Float get0() {
            Number number = this.config.get(this.path);
            if (number == null) return this.getDefaultValue();
            return number.floatValue();
        }
    }

    public static class DoubleEntry extends ConfigEntry<Double> {
        private final double minValue;
        private final double maxValue;

        public DoubleEntry(CommentedConfig config, String path, double minValue, double maxValue, double defaultValue) {
            super(config, path, defaultValue);
            this.minValue = minValue;
            this.maxValue = maxValue;

            Number n = config.get(path);
            if (n == null) {
                config.set(path, defaultValue);
            } else if (n.doubleValue() < minValue) {
                config.set(path, minValue);
            } else if (n.doubleValue() > maxValue) {
                config.set(path, maxValue);
            }
        }

        public double getMinValue() {
            return this.minValue;
        }

        public double getMaxValue() {
            return this.maxValue;
        }

        @Override
        protected Double get0() {
            Number number = this.config.get(this.path);
            if (number == null) return this.getDefaultValue();
            return number.doubleValue();
        }
    }

    public static class ConfigEntry<T> {
        protected final CommentedConfig config;
        protected final String path;
        private final T defaultValue;
        private final Class<?> type;
        private String comment;
        private T value;

        public ConfigEntry(CommentedConfig config, String path, T defaultValue) {
            this.config = config;
            this.path = path;
            this.defaultValue = defaultValue;
            this.type = defaultValue.getClass();
        }

        public T get() {
            var value0 = this.get0();
            if (value0 != null) return value0;
            var value = this.value;
            this.config.set(this.path, value);
            if (value == null) return this.defaultValue;
            return value;
        }

        protected T get0() {
            T value = this.config.get(this.path);
            if (value == null) return this.defaultValue;
            return value;
        }

        public boolean isSet() {
            return this.value != null;
        }

        public void set(T value) {
            this.config.set(this.path, value);
            this.value = value;
        }

        @SuppressWarnings("unchecked")
        private void setUnsafe(Object value) {
            if (value != null && !this.type.isInstance(value))
                throw new ClassCastException(value.getClass().getName() + " can't be cast to " + this.type.getName());
            if (value == null)
                value = this.defaultValue;

            this.config.set(this.path, value);
            this.value = (T) value;
        }

        @SuppressWarnings("unchecked")
        private boolean setSafe(Object value) {
            if (value != null && !this.type.isInstance(value))
                return false;
            if (value == null)
                value = this.defaultValue;

            this.config.set(this.path, value);
            this.value = (T) value;
            return true;
        }

        public String getComment() {
            return this.comment;
        }

        public void setComment(String comment) {
            this.config.setComment(this.path, comment);
            this.comment = comment;
        }

        public T getDefaultValue() {
            return this.defaultValue;
        }

        public T getOrDefault() {
            return this.get();
        }

        public void reset() {
            this.value = this.defaultValue;
            this.config.set(this.path, this.defaultValue);
            this.config.setComment(this.path, this.getComment());
        }

        public void inherit() {
            this.value = this.get0();
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
                var entry = new ByteEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public ShortEntry withinRange(short min, short max, short def) {
                var entry = new ShortEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public IntEntry withinRange(int min, int max, int def) {
                var entry = new IntEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public LongEntry withinRange(long min, long max, long def) {
                var longEntry = new LongEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, longEntry);
                return longEntry;
            }

            public FloatEntry withinRange(float min, float max, float def) {
                var entry = new FloatEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public DoubleEntry withinRange(double min, double max, double def) {
                var entry = new DoubleEntry(this.builder.config, this.path, min, max, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public CharEntry value(char def) {
                var entry = new CharEntry(this.builder.config, this.path, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public BooleanEntry value(boolean def) {
                var entry = new BooleanEntry(this.builder.config, this.path, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public StringEntry value(String def) {
                var entry = new StringEntry(this.builder.config, this.path, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }

            public <T extends Enum<T>> EnumEntry<T> value(T def) {
                var entry = new EnumEntry<>(this.builder.config, this.path, def);
                this.builder.entries.put(this.path, entry);
                entry.setComment(this.comment);
                return entry;
            }
        }
    }
}
