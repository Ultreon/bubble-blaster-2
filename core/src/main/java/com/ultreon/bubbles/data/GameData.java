package com.ultreon.bubbles.data;

import com.badlogic.gdx.files.FileHandle;
import com.ultreon.data.DataIo;
import com.ultreon.data.types.MapType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class GameData {
    protected abstract MapType dump(MapType tag);

    protected final void dump(FileHandle file) throws IOException {
        try (OutputStream output = file.write(false)) {
            DataIo.writeCompressed(this.dump(new MapType()), output);
        }
    }

    protected abstract void load(MapType data);

    protected final void load(FileHandle file) throws IOException {
        try (InputStream input = file.read()) {
            this.load(DataIo.<MapType>readCompressed(input));
        }
    }
}
