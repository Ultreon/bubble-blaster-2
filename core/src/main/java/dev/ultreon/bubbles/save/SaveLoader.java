package dev.ultreon.bubbles.save;

import com.badlogic.gdx.files.FileHandle;
import dev.ultreon.bubbles.common.GameFolders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final FileHandle savesDir;
    private final HashMap<String, Supplier<GameSave>> saves = new HashMap<>();

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
     * Get the save files directory.
     *
     * @return The save files directory.
     */
    @NotNull
    public FileHandle getSavesDir() {
        return this.savesDir;
    }

    /**
     * Refresh saves index.
     */
    public void refresh() {
        if (!this.savesDir.exists())
            this.savesDir.mkdirs();

        var dirs = this.savesDir.list();
        this.saves.clear();

        for (var dir : Objects.requireNonNull(dirs)) {
            Supplier<GameSave> saveSupplier = () -> GameSave.fromFile(dir);
            this.saves.put(dir.name(), saveSupplier);
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
