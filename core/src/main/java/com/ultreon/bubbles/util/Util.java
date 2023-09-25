package com.ultreon.bubbles.util;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.google.common.annotations.Beta;
import com.ultreon.bubbles.common.References;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.render.gui.screen.ScreenManager;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.io.filefilters.DirectoryFileFilter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class Util {
    @Deprecated
    public static ScreenManager getSceneManager() {
        return BubbleBlaster.getInstance().getScreenManager();
    }

    public static BubbleBlaster getGame() {
        return BubbleBlaster.getInstance();
    }

    @Deprecated
    public static BitmapFont getGameFont() {
        return BubbleBlaster.getInstance().getLogoFont();
    }

    public static void setCursor(Cursor cursor) {
        // Set the cursor to the Game Window.
        BubbleBlaster.getInstance().getGameWindow().setCursor(cursor);
    }

    public static void setCursor(Cursor.SystemCursor cursor) {
        // Set the cursor to the Game Window.
        BubbleBlaster.getInstance().getGameWindow().setCursor(cursor);
    }

    @Beta
    public static ArrayList<GameSave> getSaves() {
        ArrayList<GameSave> saves = new ArrayList<>();
        File savesDir = References.SAVES_DIR;

        File[] files = savesDir.listFiles(new DirectoryFileFilter());
        if (files == null) files = new File[]{};
        for (File save : files) {
            if (save.isDirectory()) {
                saves.add(GameSave.fromFile(save));
            }
        }

        return saves;
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T make(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }
}
