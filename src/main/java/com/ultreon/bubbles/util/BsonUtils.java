package com.ultreon.bubbles.util;

import org.bson.BsonDocument;

@Deprecated(since = "0.0.3071-indev5", forRemoval = true)
public class BsonUtils {
    @Deprecated(since = "0.0.3071-indev5", forRemoval = true)
    private BsonUtils() {
        throw ExceptionUtils.utilityClass();
    }

    @Deprecated(since = "0.0.3071-indev5", forRemoval = true)
    public static BsonDocument generateTagDocument(BsonDocument document, BsonDocument tag) {
        document.put("Tag", tag);
        return document;
    }

    @Deprecated(since = "0.0.3071-indev5", forRemoval = true)
    public static BsonDocument getTagDocument(BsonDocument document) {
        return document.getDocument("Tag");
    }
}
