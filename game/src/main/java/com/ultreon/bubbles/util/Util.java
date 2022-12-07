package com.ultreon.bubbles.util;

import com.google.common.annotations.Beta;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.common.References;
import com.ultreon.bubbles.render.screen.ScreenManager;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.io.filefilters.DirectoryFileFilter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class Util {
    public static ScreenManager getSceneManager() {
        return BubbleBlaster.getInstance().getScreenManager();
    }

    public static BubbleBlaster getGame() {
        return BubbleBlaster.getInstance();
    }

    public static Font getGameFont() {
        return BubbleBlaster.getInstance().getGameFont();
    }

    public static void setCursor(Cursor cursor) {
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
}
