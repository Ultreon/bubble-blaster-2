package com.ultreon.bubbles.api.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Event<T extends IListener> {
    private final List<T> listeners = new ArrayList<>();

    private final Class<?>[] parameterTypes;
    private final Method method;

    public Event(Class<T> listenerClass) {
        if (!listenerClass.isInterface() || !listenerClass.isAnnotationPresent(FunctionalInterface.class))
            throw new IllegalArgumentException("Class " + listenerClass.getName() + " isn't a functional interface.");

        Method[] methods = listenerClass.getMethods();
        if (methods.length > 1)
            throw new IllegalArgumentException("Class " + listenerClass.getName() + " has too many methods: " + methods.length + ". Only 1 is allowed for functional interfaces.");
        if (methods.length < 1)
            throw new IllegalArgumentException("Class " + listenerClass.getName() + " has too few methods: " + methods.length + ". Need 1 for functional interfaces.");

        this.method = methods[0];
        this.parameterTypes = method.getParameterTypes();
    }

    public void register(T listener) {
        this.listeners.add(listener);
    }

    public void post(Object... args) {
        Class<?>[] classes = Arrays.stream(args).map(Object::getClass).toList().toArray(new Class[]{});
        if (!Arrays.equals(args, classes)) {
            throw new IllegalArgumentException("Can't pass the arguments to the event with: " + Arrays.toString(parameterTypes));
        }

        for (T listener : listeners) {
            try {
                method.invoke(listener, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.err.println("Error occurred while invoking an event listener, stack trace follows:");
                e.printStackTrace(System.err);
            }
        }
    }
}
