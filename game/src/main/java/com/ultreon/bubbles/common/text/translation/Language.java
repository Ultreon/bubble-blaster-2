package com.ultreon.bubbles.common.text.translation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ultreon.bubbles.settings.GameSettings;

import java.util.Locale;

public class Language {
    private final Locale locale;
    private final JsonObject root;
    private final String id;

    public Language(Locale locale, JsonObject json, String id) {
        this.locale = locale;
        this.root = json;
        this.id = id;
    }

    public String get(String path, Object... args) {
        String[] split = path.split("/");

        JsonObject object = root;
        for (int i = 0, splitLength = split.length; i < splitLength - 1; i++) {
            String s = split[i];
            JsonElement element = object.get(s);
            if (element instanceof JsonObject obj) {
                object = obj;
            } else {
                return null;
            }
        }
        JsonElement element = object.get(split[split.length - 1]);
        if (element instanceof JsonPrimitive primitive) {
            String s = primitive.getAsString();
            return s == null ? "null" : s.formatted(args);
        }
        return null;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getId() {
        return id;
    }

    public static String translate(String path, Object... args) {
        Language language = LanguageManager.INSTANCE.get(GameSettings.instance().getLanguageLocale());
        if (language == null) return translateFallback(path, args);
        String s = language.get(path, args);
        return s == null ? translateFallback(path, args) : s;
    }

    private static String translateFallback(String path, Object[] args) {
        Language english = LanguageManager.INSTANCE.get(new Locale("en"));
        if (english == null) {
            throw new IllegalStateException("English language not loaded");
        }
        String s = english.get(path, args);
        return s == null ? path : s;
    }
}
