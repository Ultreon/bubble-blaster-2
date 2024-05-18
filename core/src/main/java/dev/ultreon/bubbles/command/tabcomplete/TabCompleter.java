package dev.ultreon.bubbles.command.tabcomplete;

import dev.ultreon.libs.commons.v0.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class TabCompleter {
    private TabCompleter() {
        throw new UnsupportedOperationException("Not allowed to initialize TabCompleter.");
    }

    public static List<String> getStrings(String arg, String... strings) {
        var list = new ArrayList<String>();
        for (var str : strings) {
            TabCompleter.addIfStartsWith(list, arg + " ", str);
        }
        return list;
    }

    public static List<String> getInts(String arg) {
        var list = new ArrayList<String>();
        for (var i = 0; i <= 9; i++) {
            list.add(arg + i);
        }
        return list;
    }

    public static List<String> getDecimals(String arg) {
        var list = new ArrayList<String>();
        list.add(arg + ".");
        for (var i = 0; i <= 9; i++) {
            list.add(arg + i);
        }
        return list;
    }

    @SuppressWarnings("UnusedReturnValue")
    private static List<String> addIfStartsWith(List<String> list, String arg, String startWith) {
        if (arg.startsWith(startWith)) {
            list.add(arg);
        }
        return list;
    }

    private static List<String> addIfStartsWith(List<String> list, Identifier arg, String startWith) {
        if (arg.path().startsWith(startWith)) {
            list.add(arg.toString());
        } else if (arg.toString().startsWith(startWith)) {
            list.add(arg.toString());
        }
        return list;
    }
}
