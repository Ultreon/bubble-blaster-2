package com.ultreon.bubbles.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class JSONUtils {
    private static final Gson GSON = (new GsonBuilder()).create();

    /**
     * Does the given JsonObject contain a string field with the given name?
     */
    public static boolean isString(JsonObject json, String memberName) {
        return JSONUtils.isJsonPrimitive(json, memberName) && json.getAsJsonPrimitive(memberName).isString();
    }

    /**
     * Is the given JsonElement a string?
     */
    public static boolean isString(JsonElement json) {
        return json.isJsonPrimitive() && json.getAsJsonPrimitive().isString();
    }

    public static boolean isNumber(JsonElement json) {
        return json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber();
    }

    public static boolean isBoolean(JsonObject json, String memberName) {
        return JSONUtils.isJsonPrimitive(json, memberName) && json.getAsJsonPrimitive(memberName).isBoolean();
    }

    /**
     * Does the given JsonObject contain an array field with the given name?
     */
    public static boolean isJsonArray(JsonObject json, String memberName) {
        return JSONUtils.hasField(json, memberName) && json.get(memberName).isJsonArray();
    }

    /**
     * Does the given JsonObject contain a field with the given name whose type is primitive (String, Java primitive, or
     * Java primitive wrapper)?
     */
    public static boolean isJsonPrimitive(JsonObject json, String memberName) {
        return JSONUtils.hasField(json, memberName) && json.get(memberName).isJsonPrimitive();
    }

    /**
     * Does the given JsonObject contain a field with the given name?
     */
    public static boolean hasField(JsonObject json, String memberName) {
        if (json == null) {
            return false;
        } else {
            return json.get(memberName) != null;
        }
    }

    /**
     * Gets the string value create the given JsonElement.  Expects the second parameter to be the name create the element's
     * field if an error message needs to be thrown.
     */
    public static String getString(JsonElement json, String memberName) {
        if (json.isJsonPrimitive()) {
            return json.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a string, was " + JSONUtils.toString(json));
        }
    }

    /**
     * Gets the string value create the field on the JsonObject with the given name.
     */
    public static String getString(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getString(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a string");
        }
    }

    /**
     * Gets the string value create the field on the JsonObject with the given name, or the given default value if the field
     * is missing.
     */
    public static String getString(JsonObject json, String memberName, String fallback) {
        return json.has(memberName) ? JSONUtils.getString(json.get(memberName), memberName) : fallback;
    }

    /**
     * Gets the boolean value create the given JsonElement.  Expects the second parameter to be the name create the element's
     * field if an error message needs to be thrown.
     */
    public static boolean getBoolean(JsonElement json, String memberName) {
        if (json.isJsonPrimitive()) {
            return json.getAsBoolean();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a Boolean, was " + JSONUtils.toString(json));
        }
    }

    /**
     * Gets the boolean value create the field on the JsonObject with the given name.
     */
    public static boolean getBoolean(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getBoolean(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Boolean");
        }
    }

    /**
     * Gets the boolean value create the field on the JsonObject with the given name, or the given default value if the field
     * is missing.
     */
    public static boolean getBoolean(JsonObject json, String memberName, boolean fallback) {
        return json.has(memberName) ? JSONUtils.getBoolean(json.get(memberName), memberName) : fallback;
    }

    /**
     * Gets the float value create the given JsonElement.  Expects the second parameter to be the name create the element's field
     * if an error message needs to be thrown.
     */
    public static float getFloat(JsonElement json, String memberName) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsFloat();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a Float, was " + JSONUtils.toString(json));
        }
    }

    /**
     * Gets the float value create the field on the JsonObject with the given name.
     */
    public static float getFloat(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getFloat(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Float");
        }
    }

    /**
     * Gets the float value create the field on the JsonObject with the given name, or the given default value if the field
     * is missing.
     */
    public static float getFloat(JsonObject json, String memberName, float fallback) {
        return json.has(memberName) ? JSONUtils.getFloat(json.get(memberName), memberName) : fallback;
    }

    /**
     * Gets a long from a JSON element and validates that the value is actually a number.
     */
    public static long getLong(JsonElement json, String memberName) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsLong();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a Long, was " + JSONUtils.toString(json));
        }
    }

    /**
     * Gets a long from a JSON element, throws an error if the member does not exist.
     */
    public static long getLong(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getLong(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Long");
        }
    }

    public static long getLong(JsonObject json, String memberName, long fallback) {
        return json.has(memberName) ? JSONUtils.getLong(json.get(memberName), memberName) : fallback;
    }

    /**
     * Gets the integer value create the given JsonElement.  Expects the second parameter to be the name create the element's
     * field if an error message needs to be thrown.
     */
    public static int getInt(JsonElement json, String memberName) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a Int, was " + JSONUtils.toString(json));
        }
    }

    /**
     * Gets the integer value create the field on the JsonObject with the given name.
     */
    public static int getInt(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getInt(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Int");
        }
    }

    /**
     * Gets the integer value create the field on the JsonObject with the given name, or the given default value if the field
     * is missing.
     */
    public static int getInt(JsonObject json, String memberName, int fallback) {
        return json.has(memberName) ? JSONUtils.getInt(json.get(memberName), memberName) : fallback;
    }

    public static byte getByte(JsonElement json, String memberName) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsByte();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a Byte, was " + JSONUtils.toString(json));
        }
    }

    public static byte getByte(JsonObject json, String memberName, byte fallback) {
        return json.has(memberName) ? JSONUtils.getByte(json.get(memberName), memberName) : fallback;
    }

    /**
     * Gets the given JsonElement as a JsonObject.  Expects the second parameter to be the name create the element's field if
     * an error message needs to be thrown.
     */
    public static JsonObject getJsonObject(JsonElement json, String memberName) {
        if (json.isJsonObject()) {
            return json.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a JsonObject, was " + JSONUtils.toString(json));
        }
    }

    public static JsonObject getJsonObject(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getJsonObject(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonObject");
        }
    }

    /**
     * Gets the JsonObject field on the JsonObject with the given name, or the given default value if the field is
     * missing.
     */
    public static JsonObject getJsonObject(JsonObject json, String memberName, JsonObject fallback) {
        return json.has(memberName) ? JSONUtils.getJsonObject(json.get(memberName), memberName) : fallback;
    }

    /**
     * Gets the given JsonElement as a JsonArray.  Expects the second parameter to be the name create the element's field if
     * an error message needs to be thrown.
     */
    public static JsonArray getJsonArray(JsonElement json, String memberName) {
        if (json.isJsonArray()) {
            return json.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a JsonArray, was " + JSONUtils.toString(json));
        }
    }

    /**
     * Gets the JsonArray field on the JsonObject with the given name.
     */
    public static JsonArray getJsonArray(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getJsonArray(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonArray");
        }
    }

    /**
     * Gets the JsonArray field on the JsonObject with the given name, or the given default value if the field is
     * missing.
     */
    @Nullable
    public static JsonArray getJsonArray(JsonObject json, String memberName, @Nullable JsonArray fallback) {
        return json.has(memberName) ? JSONUtils.getJsonArray(json.get(memberName), memberName) : fallback;
    }

    public static <T> T deserializeClass(@Nullable JsonElement json, String memberName, JsonDeserializationContext context, Class<? extends T> adapter) {
        if (json != null) {
            return context.deserialize(json, adapter);
        } else {
            throw new JsonSyntaxException("Missing " + memberName);
        }
    }

    public static <T> T deserializeClass(JsonObject json, String memberName, JsonDeserializationContext context, Class<? extends T> adapter) {
        if (json.has(memberName)) {
            return JSONUtils.deserializeClass(json.get(memberName), memberName, context, adapter);
        } else {
            throw new JsonSyntaxException("Missing " + memberName);
        }
    }

    public static <T> T deserializeClass(JsonObject json, String memberName, T fallback, JsonDeserializationContext context, Class<? extends T> adapter) {
        return json.has(memberName) ? JSONUtils.deserializeClass(json.get(memberName), memberName, context, adapter) : fallback;
    }

    /**
     * Gets a human-readable description create the given JsonElement's type.  For example: "a number (4)"
     */
    public static String toString(JsonElement json) {
        String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(json), "...", 10);
        if (json == null) {
            return "null (missing)";
        } else if (json.isJsonNull()) {
            return "null (json)";
        } else if (json.isJsonArray()) {
            return "an array (" + s + ")";
        } else if (json.isJsonObject()) {
            return "an object (" + s + ")";
        } else {
            if (json.isJsonPrimitive()) {
                JsonPrimitive jsonprimitive = json.getAsJsonPrimitive();
                if (jsonprimitive.isNumber()) {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean()) {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }

    @Nullable
    public static <T> T fromJson(Gson gsonIn, Reader readerIn, Class<T> adapter, boolean lenient) {
        try {
            JsonReader jsonreader = new JsonReader(readerIn);
            jsonreader.setLenient(lenient);
            return gsonIn.getAdapter(adapter).read(jsonreader);
        } catch (IOException ioexception) {
            throw new JsonParseException(ioexception);
        }
    }

    @Nullable
    public static <T> T fromJSON(Gson gson, Reader reader, TypeToken<T> type, boolean lenient) {
        try {
            JsonReader jsonreader = new JsonReader(reader);
            jsonreader.setLenient(lenient);
            return gson.getAdapter(type).read(jsonreader);
        } catch (IOException ioexception) {
            throw new JsonParseException(ioexception);
        }
    }

    @Nullable
    public static <T> T fromJSON(Gson gson, String string, TypeToken<T> type, boolean lenient) {
        return JSONUtils.fromJSON(gson, new StringReader(string), type, lenient);
    }

    @Nullable
    public static <T> T fromJson(Gson gsonIn, String json, Class<T> adapter, boolean lenient) {
        return JSONUtils.fromJson(gsonIn, new StringReader(json), adapter, lenient);
    }

    @Nullable
    public static <T> T fromJSONUnlenient(Gson gson, Reader reader, TypeToken<T> type) {
        return JSONUtils.fromJSON(gson, reader, type, false);
    }

    @Nullable
    public static <T> T fromJSONUnlenient(Gson gson, String string, TypeToken<T> type) {
        return JSONUtils.fromJSON(gson, string, type, false);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, Reader reader, Class<T> jsonClass) {
        return JSONUtils.fromJson(gson, reader, jsonClass, false);
    }

    @Nullable
    public static <T> T fromJson(Gson gsonIn, String json, Class<T> adapter) {
        return JSONUtils.fromJson(gsonIn, json, adapter, false);
    }

    public static JsonObject fromJson(String json, boolean lenient) {
        return JSONUtils.fromJson(new StringReader(json), lenient);
    }

    public static JsonObject fromJson(Reader reader, boolean lenient) {
        return JSONUtils.fromJson(GSON, reader, JsonObject.class, lenient);
    }

    public static JsonObject fromJson(String json) {
        return JSONUtils.fromJson(json, false);
    }

    public static JsonObject fromJson(Reader reader) {
        return JSONUtils.fromJson(reader, false);
    }
}