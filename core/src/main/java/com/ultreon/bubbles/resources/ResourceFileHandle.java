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
    private final Resource resource;

    public ResourceFileHandle(Identifier id) {
        super("assets/" + id.location() + "/" + id.path(), Files.FileType.Internal);
        this.id = id;
        this.resource = BubbleBlaster.getInstance().getResourceManager().getResource(id);
    }

    public ResourceFileHandle(Resource resource) {
        super("generated://" + UUID.randomUUID().toString().replaceAll("-", ""));
        Preconditions.checkNotNull(resource, "The 'resource' parameter should not be null");

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
        return this.resource == null ? this.readFallback() : this.resource.loadOrOpenStream();
    }

    private InputStream readFallback() {
        if (this.isGenerated())
            throw new GdxRuntimeException(new NullPointerException("Generated resource is null"));

        final var resPath = "/assets/" + this.id.location() + "/" + this.id.path();
        InputStream resourceAsStream = getClass().getResourceAsStream(resPath);

        if (resourceAsStream == null)
            throw new GdxRuntimeException(new FileNotFoundException("File resource not found: " + this.id));

        return resourceAsStream;
    }

    private boolean isGenerated() {
        return this.id.location().equals("java") && this.id.path().startsWith("generated_");
    }

    @Override
    public File file() {
        throw new GdxRuntimeException("Unsupported");
    }

    @Override
    public boolean exists() {
        return resource != null;
    }
}
