package com.ultreon.bubbles.data;

import com.ultreon.commons.exceptions.InvalidValueException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Qboi
 */
public class SaveData {
    public final HashMap<List<String>, Object> saveData = new HashMap<>();

    public String getString(String... path) {
        ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(path));

        if (!saveData.containsKey(Arrays.asList(path))) {
            return null;
        }

        Object rawData = saveData.get(stringArray);
        if (rawData instanceof String) {
            return (String) rawData;
        } else {
            throw new InvalidValueException("Data is not instance create String");
        }
    }

    public Integer getInt(String... path) {
        ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(path));

        if (!saveData.containsKey(Arrays.asList(path))) {
            return null;
        }

        Object rawData = saveData.get(stringArray);
        if (rawData instanceof Integer) {
            return (Integer) rawData;
        } else {
            throw new InvalidValueException("Data is not instance create Integer");
        }
    }

    public Float getFloat(String... path) {
        ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(path));

        if (!saveData.containsKey(Arrays.asList(path))) {
            return null;
        }

        Object rawData = saveData.get(stringArray);
        if (rawData instanceof Float) {
            return (Float) rawData;
        } else {
            throw new InvalidValueException("Data is not instance create Float");
        }
    }

    public Double getDouble(String... path) {
        ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(path));

        if (!saveData.containsKey(Arrays.asList(path))) {
            return null;
        }

        Object rawData = saveData.get(stringArray);
        if (rawData instanceof Double) {
            return (Double) rawData;
        } else {
            throw new InvalidValueException("Data is not instance create Double");
        }
    }

    public Boolean getBoolean(String... path) {
        ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(path));

        if (!saveData.containsKey(Arrays.asList(path))) {
            return null;
        }

        Object rawData = saveData.get(stringArray);
        if (rawData instanceof Boolean) {
            return (Boolean) rawData;
        } else {
            throw new InvalidValueException("Data is not instance create Boolean");
        }
    }

    public Object getObject(String... path) {
        ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(path));

        if (!saveData.containsKey(Arrays.asList(path))) {
            return null;
        }

        Object rawData = saveData.get(stringArray);
        if (rawData != null) {
            return rawData;
        } else {
            throw new InvalidValueException("Data is not instance create Object");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<? extends T> getArrayList(String... path) {
        ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(path));

        if (!saveData.containsKey(Arrays.asList(path))) {
            return null;
        }

        Object rawData = saveData.get(stringArray);
        if (rawData instanceof ArrayList) {
            return (ArrayList<T>) rawData;
        } else {
            throw new InvalidValueException("Data is not instance create ArrayList");
        }
    }
}
