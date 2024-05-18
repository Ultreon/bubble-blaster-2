package dev.ultreon.bubbles.data;

import com.badlogic.gdx.files.FileHandle;
import dev.ultreon.ubo.DataIo;
import dev.ultreon.ubo.types.MapType;

import java.io.IOException;

public abstract class GameData {
    protected abstract MapType dump(MapType tag);

    protected final void dump(FileHandle file) throws IOException {
        try (var output = file.write(false)) {
            DataIo.writeCompressed(this.dump(new MapType()), output);
        }
    }

    protected abstract void load(MapType data);

    protected final void load(FileHandle file) throws IOException {
        try (var input = file.read()) {
            this.load(DataIo.<MapType>readCompressed(input));
        }
    }
}
