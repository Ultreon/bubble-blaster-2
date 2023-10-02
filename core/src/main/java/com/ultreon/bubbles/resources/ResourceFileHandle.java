package com.ultreon.bubbles.resources;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.common.base.Preconditions;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.resources.v0.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class ResourceFileHandle extends FileHandle {
    private final Identifier id;

    public ResourceFileHandle(Identifier id) {
        super("/assets/" + id.location() + "/" + id.path(), Files.FileType.Internal);

        this.id = id;
    }
}
