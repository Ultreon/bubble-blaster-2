package com.ultreon.commons.util;

import com.ultreon.commons.exceptions.IllegalCallerException;

public class ClassUtils {
    public static String getCallerClassName() {
        var stElements = Thread.currentThread().getStackTrace();
        for (var i = 1; i < stElements.length; i++) {
            var ste = stElements[i];
            if (!ste.getClassName().equals(ClassUtils.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return stElements[i + 1].getClassName();
            }
        }

        return null;
    }

    public static Class<?> getCallerClass() {
        var className = ClassUtils.getCallerClassName();

        if (className == null) {
            return null;
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void checkCallerClassEquals(Class<?> clazz) {
        if (ClassUtils.getCallerClass() != clazz)
            throw new IllegalCallerException("Called from illegal class, valid class: " + clazz.getSimpleName());
    }

    public static void checkCallerClassExtends(Class<?> clazz) {
        if (ClassUtils.getCallerClass() == null || ClassUtils.getCallerClass().isAssignableFrom(clazz))
            throw new IllegalCallerException("Called from illegal class, valid (extendable) class: " + clazz.getSimpleName());
    }
}
