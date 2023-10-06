package com.ultreon.bubbles.render.font;

import com.ultreon.libs.commons.v0.Mth;
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
        int lastDiff = Integer.MAX_VALUE;
        Thickness cur = null;
        for (Thickness value : Thickness.values()) {
            if (Mth.diff(amount, value.amount) < lastDiff) {
                cur = value;
            }
        }

        assert cur != null;
        return cur;
    }

    @NotNull
    public static Thickness closestTo(int amount, Collection<Thickness> values) {
        int lastDiff = Integer.MAX_VALUE;
        Thickness cur = null;
        for (Thickness value : values) {
            int diff = Mth.diff(amount, value.amount);
            if (diff < lastDiff) {
                lastDiff = diff;
                cur = value;
            }
        }

        assert cur != null;
        return cur;
    }
}
