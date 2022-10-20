package com.ultreon.commons.exceptions;

import com.google.gson.JsonElement;

public class IllegalJsonElementInArray extends Throwable {
    private final int index;
    private final JsonElement element;

    public IllegalJsonElementInArray(int index, JsonElement element) {
        this.index = index;
        this.element = element;
    }

    public IllegalJsonElementInArray(String message, int index, JsonElement element) {
        super(message);
        this.index = index;
        this.element = element;
    }

    public IllegalJsonElementInArray(String message, Throwable cause, int index, JsonElement element) {
        super(message, cause);
        this.index = index;
        this.element = element;
    }

    public IllegalJsonElementInArray(Throwable cause, int index, JsonElement element) {
        super(cause);
        this.index = index;
        this.element = element;
    }

    public IllegalJsonElementInArray(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int index, JsonElement element) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.index = index;
        this.element = element;
    }

    public JsonElement getElement() {
        return element;
    }
}
