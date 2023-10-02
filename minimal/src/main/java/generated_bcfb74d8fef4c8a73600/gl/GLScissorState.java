package generated_bcfb74d8fef4c8a73600.gl;

import com.badlogic.gdx.Gdx;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GLScissorState {

    private static int currentX;
    private static int currentY;
    private static int currentW;
    private static int currentH;
    private static boolean intialized;

    public final boolean enabled;
    public final int height;
    public final int width;
    public final int x;
    public final int y;

    private GLScissorState(int x, int y, int w, int h, boolean enabled) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.enabled = enabled;
    }

    @NotNull
    public static GLScissorState captureScissor() {
        if (!intialized) {
            intialized = true;
            currentX = 0;
            currentY = 0;
            currentW = Gdx.graphics.getBackBufferWidth();
            currentH = Gdx.graphics.getBackBufferHeight();
        }

        boolean scissorEnabled = Gdx.gl.glIsEnabled(GL20.GL_SCISSOR_TEST);
        return new GLScissorState(currentX, currentY, currentW, currentH, scissorEnabled);
    }

    public static void glScissor(int x, int y, int w, int h) {
        currentX = x;
        currentY = y;
        currentW = w;
        currentH = h;
        GL11.glScissor(x, y, w, h);
    }

    public void reapplyState() {
        Gdx.gl.glScissor(this.x, this.y, this.width, this.height);
        if (Gdx.gl.glGetError() == GL20.GL_INVALID_VALUE)
            throw new IllegalStateException("Gdx.gl.glScissor raised an GL20.GL_INVALID_VALUE! Scissor state: " + this);

        if (this.enabled) {
            if (!Gdx.gl.glIsEnabled(GL20.GL_SCISSOR_TEST))
                Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        } else if (Gdx.gl.glIsEnabled(GL20.GL_SCISSOR_TEST)) {
            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        }
    }

    @Override
    public String toString() {
        return String.format("GLScissorState[x = %d (0x%X), y = %d (0x%X), w = %d (0x%X), h = %d (0x%X)]", this.x, this.x, this.y, this.y, this.width, this.width, this.height, this.height);
    }
}
