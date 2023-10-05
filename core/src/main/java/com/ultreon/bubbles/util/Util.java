package com.ultreon.bubbles.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.google.common.annotations.Beta;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.GameFolders;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.io.filefilters.DirectoryFileFilter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class Util {

    public static BubbleBlaster getGame() {
        return BubbleBlaster.getInstance();
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
        FileHandle savesDir = GameFolders.SAVES_DIR;

        FileHandle[] files = savesDir.list(new DirectoryFileFilter());
        if (files == null) files = new FileHandle[]{};
        for (FileHandle save : files) {
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
