package com.ultreon.bubbles.common.text.translation;

import com.google.gson.*;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class LanguageManager {
    public static final LanguageManager INSTANCE = new LanguageManager();
    private final Map<String, Language> languages = new HashMap<>();
    private final Set<String> locales = new HashSet<>();
    private final Set<String> ids = new HashSet<>();
    private final Map<String, String> locale2id = new HashMap<>();
    private final Map<String, String> id2locale = new HashMap<>();
    final Logger logger = LogManager.getLogger("Language-Manager");

    private LanguageManager() {

    }

    public Language load(Locale locale, String id, ResourceManager resourceManager) {
        Gson gson = new Gson();
        String s = "languages/" + id + ".json";
        List<byte[]> assets = resourceManager.getAllAssetsByPath(s);
        Set<Identifier> assets1 = resourceManager.getAssets().keySet();
        JsonObject json = new JsonObject();
        for (byte[] asset : assets) {
            JsonObject object = gson.fromJson(new StringReader(new String(asset, StandardCharsets.UTF_8)), JsonObject.class);
            recurse(json, object);
        }

        Language language = new Language(locale, json, id);
        languages.put(locale.getLanguage(), language);
        return language;
    }

    public Language get(Locale locale) {
        return languages.get(locale.getLanguage());
    }

    public void register(Locale locale, String id) {
        if (locales.contains(locale.getLanguage())) {
            logger.warn("Locale overridden: " + locale.getLanguage());
        }
        if (ids.contains(id)) {
            logger.warn("LanguageID overridden: " + id);
        }

        this.locales.add(locale.getLanguage());
        this.ids.add(id);
        this.locale2id.put(locale.getLanguage(), id);
        this.id2locale.put(id, locale.getLanguage());
    }

    public Locale getLocale(String id) {
        return new Locale(id2locale.get(id));
    }

    public String getLanguageID(Locale locale) {
        return locale2id.get(locale.getLanguage());
    }

    private void recurse(JsonObject json, JsonObject object) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            JsonElement value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof JsonObject obj) {
                if (json.has(key)) {
                    if (json.get(key) instanceof JsonObject) {
                        recurse(json.getAsJsonObject(key), obj);
                    }
                }
            }
            if (value instanceof JsonArray) {
                throw new JsonParseException("Not allowed to have json arrays in language json.");
            }
            json.add(key, value);
        }
    }

    public Set<Locale> getLocales() {
        return locales.stream().map(Locale::new).collect(Collectors.toSet());
    }

    public Set<String> getLanguageIDs() {
        return ids;
    }

    public List<Language> getLanguages() {
        return new ArrayList<>(languages.values());
    }
}
