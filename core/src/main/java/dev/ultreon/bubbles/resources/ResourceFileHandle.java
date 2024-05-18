package dev.ultreon.bubbles.resources;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import dev.ultreon.libs.commons.v0.Identifier;

public class ResourceFileHandle extends FileHandle {
    private final Identifier id;

    public ResourceFileHandle(Identifier id) {
        super("/assets/" + id.location() + "/" + id.path(), Files.FileType.Internal);

        this.id = id;
    }
}
