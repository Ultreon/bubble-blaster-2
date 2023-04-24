package com.ultreon.bubbles.data;

import com.ultreon.data.DataIo;
import com.ultreon.data.types.MapType;

import java.io.File;
import java.io.IOException;

public abstract class GameData {
    protected abstract MapType dump(MapType tag);

    protected final void dump(File file) throws IOException {
        DataIo.writeCompressed(dump(new MapType()), file);
    }

    protected abstract void load(MapType data);

    protected final void load(File file) throws IOException {
        load(DataIo.<MapType>readCompressed(file));
    }
}
