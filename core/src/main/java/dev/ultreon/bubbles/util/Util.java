package dev.ultreon.bubbles.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.common.GameFolders;
import dev.ultreon.bubbles.save.GameSave;
import dev.ultreon.bubbles.util.io.DirectoryFileFilter;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Deprecated(forRemoval = true)
public class Util {
    @Deprecated(forRemoval = true)
    public static BubbleBlaster getGame() {
        return BubbleBlaster.getInstance();
    }

    @Deprecated(forRemoval = true)
    public static void setCursor(Cursor cursor) {
        // Set the cursor to the Game Window.
        BubbleBlaster.getInstance().getGameWindow().setCursor(cursor);
    }

    @Deprecated(forRemoval = true)
    public static void setCursor(Cursor.SystemCursor cursor) {
        // Set the cursor to the Game Window.
        BubbleBlaster.getInstance().getGameWindow().setCursor(cursor);
    }

    @Deprecated(forRemoval = true)
    public static ArrayList<GameSave> getSaves() {
        var saves = new ArrayList<GameSave>();
        var savesDir = GameFolders.SAVES_DIR;

        var files = savesDir.list(new DirectoryFileFilter());
        if (files == null) files = new FileHandle[]{};
        for (var save : files) {
            if (save.isDirectory()) {
                saves.add(GameSave.fromFile(save));
            }
        }

        return saves;
    }

    @Deprecated(forRemoval = true)
    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    @Deprecated(forRemoval = true)
    public static <T> T make(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }
}
