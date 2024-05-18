package dev.ultreon.bubbles.debug;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class FormatterRegistry {
    private static final Map<String, Formatter<?>> FORMATTERS = new HashMap<>();

    private FormatterRegistry() {

    }

    public static  <T> Formatter<T> register(Formatter<T> formatter) {
        Class<?> clazz = formatter.clazz();
        FORMATTERS.put(clazz.getName(), formatter);
        return formatter;
    }

    public static void dump() {
        System.out.println("-=====- DEBUG FORMATTER REGISTRY DUMP -=====-");
        for (var entry : FORMATTERS.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().registryName());
        }
        System.out.println("_______ DEBUG FORMATTER REGISTRY DUMP _______");
    }

    @Nullable
    public static Formatter<?> identify(Class<?> aClass) {
        for (var clazz = aClass; clazz != null; clazz = clazz.getSuperclass()) {
            if (FORMATTERS.containsKey(clazz.getName())) {
                return FORMATTERS.get(clazz.getName());
            }
            for (var inter : clazz.getInterfaces()) {
                var identify = FormatterRegistry.identify(inter);
                if (identify != null) return identify;
            }
        }
        return null;
    }
}
