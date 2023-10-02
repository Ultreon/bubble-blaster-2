package com.ultreon.bubbles.save;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.GameFolders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Save loader for saved game instances.
 *
 * @author XyperCode (XyperCode)
 */
public class SaveLoader {
    // Static
    private static final SaveLoader instance = new SaveLoader();

    // Non-static.
    private final File savesDir;
    private final HashMap<String, Supplier<GameSave>> saves = new HashMap<>();
    private final BubbleBlaster game = BubbleBlaster.getInstance();

    /**
     * Get the {@link SaveLoader save laoder} instance.
     *
     * @return the requested instance.
     */
    @NotNull
    public static SaveLoader instance() {
        return instance;
    }

    /**
     * Save loader constructor.
     */
    private SaveLoader() {
        this.savesDir = GameFolders.SAVES_DIR;
    }

    /**
     * Get the saves directory.
     *
     * @return The saves directory.
     */
    @NotNull
    public File getSavesDir() {
        return this.savesDir;
    }

    /**
     * Refresh saves index.
     */
    public void refresh() {
        if (!this.savesDir.exists() && !this.savesDir.mkdirs())
            throw new IllegalStateException("Saves directory wasn't created.");

        File[] dirs = this.savesDir.listFiles();
        this.saves.clear();

        for (File dir : Objects.requireNonNull(dirs)) {
            Supplier<GameSave> saveSupplier = () -> GameSave.fromFile(dir);
            this.saves.put(dir.getName(), saveSupplier);
        }
    }

    @Nullable
    public GameSave getSavedGame(String name) {
        return this.saves.get(name).get();
    }

    /**
     * Get saved games.
     *
     * @return a collection create suppliers create saved games.
     */
    @NotNull
    public Collection<Supplier<GameSave>> getSaves() {
        return this.saves.values();
    }
}
