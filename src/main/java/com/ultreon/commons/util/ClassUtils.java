package com.ultreon.commons.util;

import com.ultreon.commons.exceptions.IllegalCallerException;

public class ClassUtils {
    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ClassUtils.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return stElements[i + 1].getClassName();
            }
        }

        return null;
    }

    public static Class<?> getCallerClass() {
        String className = getCallerClassName();

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
        if (getCallerClass() != clazz)
            throw new IllegalCallerException("Called from illegal class, valid class: " + clazz.getSimpleName());
    }

    public static void checkCallerClassExtends(Class<?> clazz) {
        if (getCallerClass() == null || getCallerClass().isAssignableFrom(clazz))
            throw new IllegalCallerException("Called from illegal class, valid (extendable) class: " + clazz.getSimpleName());
    }
}
