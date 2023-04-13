package com.ultreon.bubbles.render.font;

import com.ultreon.bubbles.util.helpers.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public enum Thickness {
    BLACK(900), EXTRA_BOLD(800), BOLD(700), SEMI_BOLD(600), MEDIUM(500), REGULAR(400), LIGHT(300), EXTRA_LIGHT(200), THIN(100);

    final int amount;

    Thickness(int amount) {
        this.amount = amount;
    }

    @NotNull
    public static Thickness closestTo(int amount) {
        var lastDiff = Integer.MAX_VALUE;
        Thickness cur = null;
        for (Thickness value : values()) {
            if (Mth.diff(amount, value.amount) < lastDiff) {
                cur = value;
            }
        }

        assert cur != null;
        return cur;
    }

    @NotNull
    public static Thickness closestTo(int amount, Collection<Thickness> values) {
        var lastDiff = Integer.MAX_VALUE;
        Thickness cur = null;
        for (Thickness value : values) {
            if (Mth.diff(amount, value.amount) < lastDiff) {
                cur = value;
            }
        }

        assert cur != null;
        return cur;
    }
}
