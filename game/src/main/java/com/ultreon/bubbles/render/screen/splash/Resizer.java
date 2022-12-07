package com.ultreon.bubbles.render.screen.splash;

import com.ultreon.bubbles.vector.Vec2f;

public class Resizer {
    private final float ratio;
    private final float relativeRatio;
    private final Orientation orientation;
    private final float sourceWidth;
    private final float sourceHeight;

    public Resizer(float srcWidth, float srcHeight) {
        this.ratio = srcWidth / srcHeight;

        if (srcWidth > srcHeight) {
            this.relativeRatio = srcWidth / srcHeight;
            this.orientation = Orientation.LANDSCAPE;
        } else if (srcWidth < srcHeight) {
            this.relativeRatio = srcHeight / srcWidth;
            this.orientation = Orientation.PORTRAIT;
        } else {
            this.relativeRatio = 1;
            this.orientation = Orientation.SQUARE;
        }

        this.sourceWidth = srcWidth;
        this.sourceHeight = srcHeight;
    }

    public Vec2f thumbnail(float maxWidth, float maxHeight) {
        float aspectRatio;
        float width;
        float height;

        if (sourceWidth < sourceHeight) {
            aspectRatio = (float) (sourceWidth / (double) sourceHeight);

            width = maxWidth;
            height = (int) (width / aspectRatio);

            if (height < maxHeight) {
                aspectRatio = (float) (sourceHeight / (double) sourceWidth);

                height = maxHeight;
                width = (int) (height / aspectRatio);
            }
        } else {
            aspectRatio = (float) (sourceHeight / (double) sourceWidth);

            height = maxHeight;
            width = (int) (height / aspectRatio);
            if (width < maxWidth) {
                aspectRatio = (float) (sourceWidth / (double) sourceHeight);

                width = maxWidth;
                height = (int) (width / aspectRatio);
            }
        }

        return new Vec2f(width, height);
    }

    /**
     * Aspect ratio orientation.
     */
    public enum Orientation {
        LANDSCAPE,
        SQUARE,
        PORTRAIT
    }

    public float getRatio() {
        return ratio;
    }

    public float getRelativeRatio() {
        return relativeRatio;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public float getSourceWidth() {
        return sourceWidth;
    }

    public float getSourceHeight() {
        return sourceHeight;
    }
}
