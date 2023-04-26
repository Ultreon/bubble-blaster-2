package com.ultreon.bubbles.resources;

import com.badlogic.gdx.files.FileHandle;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.resources.v0.Resource;

import java.io.InputStream;
import java.util.UUID;

public class ResourceFileHandle extends FileHandle {
    private final Identifier id;
    private Resource resource;

    public ResourceFileHandle(Identifier id) {
        super(id.toString());
        this.id = id;
        this.resource = BubbleBlaster.getInstance().getResourceManager().getResource(id);
    }

    public ResourceFileHandle(Resource resource) {
        super("generated_" + UUID.randomUUID().toString().replaceAll("-", ""));
        this.id = new Identifier("java", "generated_" + UUID.randomUUID().toString().replaceAll("-", ""));
        this.resource = resource;
    }

    public Identifier getId() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public InputStream read() {
        if (resource == null) return null;
        return resource.loadOrOpenStream();
    }

    @Override
    public boolean exists() {
        return resource != null;
    }
}
