/////////////////////
//     Package     //
/////////////////////
package dev.ultreon.bubbles.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.FilmGrainEffect;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import com.crashinvaders.vfx.effects.NfaaEffect;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBufferPool;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import dev.ultreon.bubbles.Axis2D;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.BubbleBlasterConfig;
import dev.ultreon.bubbles.config.Config;
import dev.ultreon.bubbles.debug.Debug;
import dev.ultreon.bubbles.random.JavaRandom;
import dev.ultreon.bubbles.render.gui.border.Border;
import dev.ultreon.bubbles.util.StringUtils;
import dev.ultreon.libs.commons.v0.Anchor;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.commons.v0.Pixel;
import dev.ultreon.libs.text.v1.TextObject;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus;
import space.earlygrey.shapedrawer.DefaultSideEstimator;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;

/**
 * Renderer class.
 *
 * @author XyperCode
 * @see FontRenderContext
 * @see Color
 * @see String
 * @see ImageObserver
 * @see AffineTransform
 * @see AttributedCharacterIterator
 * @see GlyphVector
 * @see Polygon
 * @see Rectangle
 */
@SuppressWarnings({"FieldCanBeLocal"})
public class Renderer {
    private static final Color ANIM_COLOR_1 = Color.rgb(0x00a0ff);
    private static final Color ANIM_COLOR_2 = Color.rgb(0x00ffa0);
    private static final Color ANIM_ERROR_COLOR_1 = Color.rgb(0xff3000);
    private static final Color ANIM_ERROR_COLOR_2 = Color.rgb(0xffa000);
    private static final Matrix4 ORIGIN = new Matrix4();
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final GL20 gl20;
    private final GL30 gl30;
    private final SpriteBatch batch;
    private final ShapeRenderer shapes;
    private final OrthographicCamera camera;
    private final DefaultSideEstimator sides;
    private final VfxManager vfxManager;
    private final GaussianBlurEffect vfxBlur;
    private final NfaaEffect vfxNfaa;
    private float lineThickness;
    private final MatrixStack matrixStack;
    private final MatrixStack globalMatrixStack;
    private Texture texture;
    private BitmapFont font;
    private final ThreadLocal<GlyphLayout> glyphLayout = new ThreadLocal<>();
    private final Color clearColor = Color.BLACK;
    private Color color;
    private boolean rendering;
    private final Matrix4 backupMatrix = new Matrix4();
    private volatile boolean triggerScissorLog = false; // TODO: DEBUG
    private boolean triggeredScissorLog = false; // TODO: DEBUG
    private boolean loggingScissors = false; // TODO: DEBUG
    private int scissorDepth = 0;
    private final Vector2 tmp2A = new Vector2();
    private final Vector2 tmp2B = new Vector2();
    private final Vector3 tmp3A = new Vector3();
    private final Vector3 tmp3B = new Vector3();
    private final Matrix4 globalTransform = new Matrix4();
    private State state;
    private boolean stateChange = true;
    private boolean blendingEnabled = true;
    private final Stack<VfxFrameBuffer> fboStack = new Stack<>();
    private final FboPool fboPool;
    private boolean depthEnabled;
    private boolean blurring;
    private final GlyphLayout layout = new GlyphLayout();
    private boolean hovering;
    private boolean hideCursor;
    private final List<Disposable> toDispose = new ArrayList<>();
    private final FilmGrainEffect vfxNoise;
    private boolean noising;
    private FrameBuffer grid;
    private int width;
    private int height;
    private ShaderProgram gridShader;
    private ShaderProgram blurShader;
    private TextureRegion white;
    private boolean blurred;

    @ApiStatus.Internal
    public Renderer(ShapeRenderer shapes, SpriteBatch batch, OrthographicCamera camera) {
        this.font = this.game.getBitmapFont();
        this.gl20 = Gdx.gl20;
        this.gl30 = Gdx.gl30;
        this.batch = batch;
        this.shapes = shapes;
        this.camera = camera;
        this.matrixStack = new MatrixStack();
        this.globalMatrixStack = new MatrixStack();
        this.matrixStack.stack.removeLast();
        this.sides = new DefaultSideEstimator(20, 4000, 3600f);

        // Projection matrix.
        this.matrixStack.onEdit = m -> {
            this.shapes.setTransformMatrix(m);
            this.batch.setTransformMatrix(m);
        };

        this.shapes.setAutoShapeType(true);

        // Visual Effects setup.
        this.vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        this.vfxManager.setBlendingEnabled(false);
        this.vfxBlur = new GaussianBlurEffect(GaussianBlurEffect.BlurType.Gaussian5x5);
        this.vfxBlur.setPasses(10);
//        this.vfxBlur.setAmount(15);
        this.vfxNfaa = new NfaaEffect(true);
        this.vfxNoise = new FilmGrainEffect();
        this.vfxNoise.setNoiseAmount(0.25f);

        this.fboPool = new FboPool(Pixmap.Format.RGBA8888, this.getWidth(), this.getHeight(), 10);
        this.fboPool.setTextureParams(TextureWrap.Repeat, TextureWrap.Repeat, TextureFilter.Linear, TextureFilter.Linear);

        this.gridShader = new ShaderProgram(this.VERT, this.GRID_FRAG);
        this.blurShader = new ShaderProgram(this.VERT, this.FRAG);
        var pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE.toGdx());
        pixmap.fill();
        var texture1 = new Texture(pixmap);
        white = new TextureRegion(texture1);
    }

    @ApiStatus.Internal
    public void begin() {
        if (this.rendering)
            throw new IllegalStateException("Renderer is already rendering");

        this.toDispose.forEach(Disposable::dispose);
        this.toDispose.clear();

        this.hovering = false;
        this.hideCursor = false;

        this.backupMatrix.set(this.shapes.getTransformMatrix());
        this.matrixStack.stack.addLast(this.shapes.getTransformMatrix());

        if (this.triggerScissorLog) {
            this.triggerScissorLog = false;
            this.triggeredScissorLog = true;
            this.loggingScissors = true;
        }

        this.globalTransform.set(ORIGIN);

        this.batch.begin();
        this.state = State.BATCH;

        this.rendering = true;

        this.clear();
        this.enableBlend();
        this.enableDepthTest();
    }

    @ApiStatus.Internal
    public void end() {
        if (!this.rendering)
            throw new IllegalStateException("Renderer isn't rendering yet");

        if (this.vfxManager.isCapturing())
            throw new IllegalStateException("Can´t end renderer while " + this.vfxManager.getClass().getSimpleName() + " is still capturing.");

        if (this.vfxManager.isApplyingEffects())
            throw new IllegalStateException("Can´t end renderer while " + this.vfxManager.getClass().getSimpleName() + " is still applying effects.");

        if (this.blurring)
            throw new IllegalStateException("Can´t end renderer while blurring mode is still enabled.");

        if (VfxFrameBuffer.getBufferNesting() != 0)
            throw new IllegalStateException("Renderer hasn't ended with FBO nesting cleared.");

        this.fboPool.freeAll();

        this.shapes.setTransformMatrix(this.backupMatrix);
        this.batch.setTransformMatrix(this.backupMatrix);

        if (!this.matrixStack.isClear())
            this.clearMatrixStack();

        this.matrixStack.stack.removeLast();

        this.disableDepthTest();
        this.disableBlend();

        if (this.batch.isDrawing()) {
            this.batch.flush();
            this.batch.end();
        }
        if (this.shapes.isDrawing()) {
            this.shapes.flush();
            this.shapes.end();
        }

        if (this.triggeredScissorLog) {
            this.triggeredScissorLog = false;
            this.loggingScissors = false;
        }

        if (this.hideCursor) {
            Gdx.input.setCursorCatched(true);
        } else {
            Gdx.input.setCursorCatched(false);
            Gdx.graphics.setCursor(this.hovering ? this.game.handCursor : this.game.arrowCursor);
        }

        this.rendering = false;
    }

    public void enableBlend() {
        this.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.blendingEnabled = true;
    }

    private void enableDepthTest() {
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        this.depthEnabled = true;
    }

    public void disableBlend() {
        this.blendingEnabled = false;
        Gdx.gl20.glDisable(GL20.GL_BLEND);
    }

    public void disableDepthTest() {
        this.depthEnabled = false;
        Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);
    }

    public void outline(Shape2D shape, Color color) {
        if (shape instanceof Circle) {
            var circle = (Circle) shape;
            this.outline(circle, color);
        } else if (shape instanceof Rectangle) {
            var rectangle = (Rectangle) shape;
            this.outline(rectangle, color);
        } else if (shape instanceof Polygon) {
            var polygon = (Polygon) shape;
            this.outline(polygon, color);
        } else if (shape instanceof Ellipse) {
            var ellipse = (Ellipse) shape;
            this.outline(ellipse, color);
        } else throw new UnsupportedOperationException("Shape not supported: " + shape.getClass().getName());
    }

    public void outline(Rectangle rect, Color color) {
        if (!this.rendering) return;

        this.box(rect.x, rect.y, rect.width, rect.height, color);
    }

    public void outline(Ellipse ellipse, Color color) {
        if (!this.rendering) return;

        this.ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height, color);
    }

    public void outline(Circle ellipse, Color color) {
        if (!this.rendering) return;

        this.circle(ellipse.x, ellipse.y, ellipse.radius, color);
    }

    public void fillCircle(float x, float y, float size, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());
        this.shapes.circle(x, y, size / 2f, this.sides.estimateSidesRequired(1f, size / 2f, size / 2f));
    }

    public void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());
        this.shapes.triangle(x1, y1, x2, y2, x3, y3);
    }

    public void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Color color1, Color color2, Color color3) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.triangle(x1, y1, x2, y2, x3, y3, color1.toGdx(), color2.toGdx(), color3.toGdx());
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(color.toGdx());
        this.shapes.triangle(x1, y1, x2, y2, x3, y3);
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3, Color color1, Color color2, Color color3) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.triangle(x1, y1, x2, y2, x3, y3, color1.toGdx(), color2.toGdx(), color3.toGdx());
    }

    public void circle(float x, float y, float size, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(color.toGdx());
        this.shapes.circle(x, y, size / 2f, this.sides.estimateSidesRequired(1, size, size));
    }

    public void fill(Shape2D s, Color color) {
        if (!this.rendering) return;

        if (s instanceof Circle) {
            var circle = (Circle) s;
            this.fill(circle, color);
        } else if (s instanceof Ellipse) {
            var ellipse = (Ellipse) s;
            this.fill(ellipse, color);
        } else if (s instanceof Rectangle) {
            var rect = (Rectangle) s;
            this.fill(rect, color);
        } else if (s instanceof Polygon) {
            var rect = (Polygon) s;
            this.fill(rect, color);
        } else if (s instanceof Polyline) {
            var rect = (Polyline) s;
            this.fill(rect, color);
        }
    }

    private void enableEffect() {
        /* Clear our depth buffer info from previous frame. */
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        /* Enable depth writing. */
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        /* Disable RGBA color writing. */
        Gdx.gl.glColorMask(false, false, false, false);
    }

    private void disableEffect() {
        /* Enable RGBA color writing. */
        Gdx.gl.glColorMask(true, true, true, true);

        /* Set the depth function to EQUAL. */
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        /* Render masked elements. */
        this.fillEffect(0, 0, this.getWidth(), this.getHeight());

        /* Disable depth writing. */
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    @ApiStatus.Experimental
    public void enableBlur(int radius) {
        if (this.blurring) {
            throw new IllegalStateException("Can't enable blur while already enabled!");
        }

        this.toBatch();
        this.batch.flush();
        this.batch.end();

        this.vfxManager.cleanUpBuffers(Color.BLACK.toGdx());
        this.fboStack.push(this.beginCapture());
        var b = this.pushScissor(0, 0, this.getWidth(), this.getHeight());
        this.blurring = true;

        this.batch.begin();
    }

    @ApiStatus.Experimental
    public void disableBlur() {
        this.toBatch();
        this.batch.end();

        this.blurring = false;
        this.popScissor();

        // Add blur effect.
        this.vfxManager.addEffect(this.vfxBlur);

        this.vfxManager.useAsInput(this.endCapture(this.fboStack.pop()));

        this.batch.begin();

        // Apply the effects chain to the captured frame.
        // In our case, only one effect (gaussian blur) will be applied.
        this.vfxManager.applyEffects();

        // Render result to the screen.
        this.vfxManager.renderToScreen();

        // Add blur effect.
        this.vfxManager.removeAllEffects();

        if (this.blendingEnabled && !Gdx.gl20.glIsEnabled(GL20.GL_BLEND)) {
            Gdx.gl20.glEnable(GL20.GL_BLEND);
            Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        if (this.depthEnabled && !Gdx.gl20.glIsEnabled(GL20.GL_DEPTH_TEST)) {
            Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        }
    }

    @ApiStatus.Experimental
    public void enableNoise() {
        if (this.noising) {
            throw new IllegalStateException("Can't enable blur while already enabled!");
        }

        this.toBatch();
        this.batch.flush();
        this.batch.end();
        this.vfxNoise.setSeed(new JavaRandom().nextFloat(-Float.MAX_VALUE, Float.MAX_VALUE));

        this.vfxManager.cleanUpBuffers(Color.BLACK.toGdx());
        this.fboStack.push(this.beginCapture());
        var b = this.pushScissor(0, 0, this.getWidth(), this.getHeight());
        this.noising = true;

        this.batch.begin();
    }

    @ApiStatus.Experimental
    public void disableNoise() {
        this.toBatch();
        this.batch.end();

        this.noising = false;
        this.popScissor();

        // Add blur effect.
        this.vfxManager.addEffect(this.vfxNoise);

        this.vfxManager.useAsInput(this.endCapture(this.fboStack.pop()));

        this.batch.begin();

        // Apply the effects chain to the captured frame.
        // In our case, only one effect (gaussian blur) will be applied.
        this.vfxManager.applyEffects();

        // Render result to the screen.
        this.vfxManager.renderToScreen();

        // Add blur effect.
        this.vfxManager.removeAllEffects();

        if (this.blendingEnabled && !Gdx.gl20.glIsEnabled(GL20.GL_BLEND)) {
            Gdx.gl20.glEnable(GL20.GL_BLEND);
            Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        if (this.depthEnabled && !Gdx.gl20.glIsEnabled(GL20.GL_DEPTH_TEST)) {
            Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        }
    }

    @ApiStatus.Experimental
    private void blur(VfxFrameBuffer fbo, int x, int y, int width, int height) {
        this.toBatch();

        // Add blur effect.
        this.vfxManager.addEffect(this.vfxBlur);
        this.vfxManager.useAsInput(fbo);

        // Apply the effects chain to the captured frame.
        // In our case, only one effect (gaussian blur) will be applied.
        this.vfxManager.applyEffects();

        // Render result to the screen.
        this.flush();
        this.scissored(x, y, width, height, this.vfxManager::renderToScreen);

        // Add blur effect.
        this.vfxManager.removeEffect(this.vfxBlur);
    }

    @CheckReturnValue
    @ApiStatus.Experimental
    public VfxFrameBuffer beginCapture() {
        var fbo = this.fboPool.obtain();
        fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        return fbo;
    }

    @CanIgnoreReturnValue
    @ApiStatus.Experimental
    public VfxFrameBuffer endCapture(VfxFrameBuffer fbo) {
        fbo.end();
        return fbo;
    }

    @CheckReturnValue
    @ApiStatus.Experimental
    public VfxFrameBuffer capture(Runnable func) {
        var fbo = this.beginCapture();
        func.run();
        return this.endCapture(fbo);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        // Resize the sprite batch and shape renderer.
        this.batch.setProjectionMatrix(this.batch.getProjectionMatrix().setToOrtho(0, width, height, 0, 0, 1000000));
        this.shapes.setProjectionMatrix(this.shapes.getProjectionMatrix().setToOrtho(0, width, height, 0, 0, 1000000));

        // Resize the FBO pool.
        this.fboPool.resize(width, height);

        // VfxManager manages internal off-screen buffers,
        // which should always match the required viewport (whole screen in our case).
        this.vfxManager.resize(width, height);

        this.resizeGrid(width, height);
    }

    public void update(float deltaTime) {
        this.vfxManager.update(deltaTime);
    }

    private void drawMasks(Runnable func) {
        /* Clear our depth buffer info from previous frame. */
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        /* Enable depth writing. */
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        /* Disable RGBA color writing. */
        Gdx.gl.glColorMask(false, false, false, false);

        /* Render mask elements. */
        func.run();
        this.shapes.flush();
    }

    private void drawMasked() {
        /* Enable RGBA color writing. */
        Gdx.gl.glColorMask(true, true, true, true);

        /* Set the depth function to EQUAL. */
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        /* Render masked elements. */
        this.fillEffect(0, 0, this.getWidth(), this.getHeight());
        this.shapes.flush();

        /* Disable depth writing. */
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    private void drawMasked(float speed) {
        /* Enable RGBA color writing. */
        Gdx.gl.glColorMask(true, true, true, true);

        /* Set the depth function to EQUAL. */
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        /* Render masked elements. */
        this.fillEffect(0, 0, this.getWidth(), this.getHeight(), speed);
        this.shapes.flush();

        /* Disable depth writing. */
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    public void withEffect(Runnable func) {
        this.toShapes();
        this.flush();
        this.disableStateChange();
        this.disableDepthTest();
        this.drawMasks(func);
        this.drawMasked();
        this.enableDepthTest();
        this.enableStateChange();
        this.flush();
    }

    public void withEffect(float speed, Runnable func) {
        this.toShapes();
        this.flush();
        this.disableStateChange();
        this.disableDepthTest();
        this.drawMasks(func);
        this.drawMasked(speed);
        this.enableDepthTest();
        this.enableStateChange();
        this.flush();
    }

    private void disableStateChange() {
        this.stateChange = false;
    }

    private void enableStateChange() {
        this.stateChange = true;
    }

    public void fill(Circle ellipse, Color color) {
        if (!this.rendering) return;

        this.fillCircle(ellipse.x, ellipse.y, ellipse.radius, color);
    }

    public void fill(Ellipse ellipse, Color color) {
        if (!this.rendering) return;

        this.fillEllipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height, color);
    }

    public void fill(Rectangle r, Color color) {
        if (!this.rendering) return;

        this.fill(r.getX(), r.getY(), r.getWidth(), r.getHeight(), color);
    }

    public void fill(Polygon polygon, Color color) {
        if (!this.rendering) return;

        this.fillPolygon(polygon, color);
    }

    public void fill(Polyline polyline, Color color) {
        if (!this.rendering) return;

        this.polyline(polyline, color);
    }

    public void fillGradient(Rectangle bounds, Color color1, Color color2) {
        if (!this.rendering) return;

        this.fillGradient(bounds.x, bounds.y, bounds.width, bounds.height, color1, color2, Axis2D.VERTICAL);
    }

    public void fillGradient(Rectangle bounds, Color color1, Color color2, Axis2D axis) {
        if (!this.rendering) return;

        this.fillGradient(bounds.x, bounds.y, bounds.width, bounds.height, color1, color2, axis);
    }

    public void fillGradient(float x, float y, float width, float height, Color color1, Color color2) {
        if (!this.rendering) return;

        this.fillGradient(x, y, width, height, color1, color2, Axis2D.VERTICAL);
    }

    public void fillGradient(float x, float y, float width, float height, Color color1, Color color2, Axis2D axis) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        switch (axis) {
            case HORIZONTAL:
                this.shapes.rect(x, y, width, height, color2.toGdx(), color1.toGdx(), color1.toGdx(), color2.toGdx());
                break;
            case VERTICAL:
                this.shapes.rect(x, y, width, height, color1.toGdx(), color1.toGdx(), color2.toGdx(), color2.toGdx());
                break;
        }
    }

    public void line(float x1, float y1, float x2, float y2, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(color.toGdx());
        this.shapes.line(x1, y1, x2, y2);
    }

    public void line(float x1, float y1, float x2, float y2, Color color1, Color color2) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.line(x1, y1, x2, y2, color1.toGdx(), color2.toGdx());
    }

    public void box(Rectangle bounds, Color color) {
        this.box(bounds, color, new Insets(1));
    }

    public void box(Rectangle bounds, Color color, Insets insets) {
        this.box(bounds.x, bounds.y, bounds.width, bounds.height, color, insets);
    }

    public void box(float x, float y, float width, float height, Color color) {
        if (!this.rendering) return;

        var border = new Border(1);
        border.setColor(color);
        border.drawBorder(this, x, y, width, height);
    }

    public void box(int x, int y, int width, int height, Color color, Insets insets) {
        if (!this.rendering) return;

        var border = new Border(insets);
        border.setColor(color);
        border.drawBorder(this, x, y, width, height);
    }

    public void box(float x, float y, float width, float height, Color color, Insets insets) {
        if (!this.rendering) return;

        var border = new Border(insets);
        border.setColor(color);
        border.drawBorder(this, (int) x, (int) y, (int) width, (int) height);
    }

    public void fill(float x, int y, float width, int height, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());
        this.shapes.rect(x, y, width, height);
    }

    public void fill(float x, float y, float width, float height, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());
        this.shapes.rect(x, y, width, height);
    }

    @ApiStatus.Experimental
    public void fillBlurred(VfxFrameBuffer fbo, int x, int y, int width, int height, Color color) {
        if (!this.rendering) return;

        this.flush();

        this.blur(fbo, x, y, width, height);
        this.flush();

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());
        this.shapes.rect(x, y, width, height);
    }

    @Language("GLSL")
    final String VERT =
            "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "\n" +
            "varying vec4 vColor;\n" +
            "varying vec2 vTexCoord;\n" +
            "\n" +
            "void main() {\n" +
            "	vColor = a_color;\n" +
            "	vTexCoord = a_texCoord0;\n" +
            "	gl_Position =  u_projTrans * a_position;\n" +
            "}\n";

    @Language("GLSL")
    final String FRAG =
            "#version 130\n" +
            "\n" +
            "// Fragment shader\n" +
            "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "\n" +
            "varying vec4 vColor;\n" +
            "varying vec2 vTexCoord;\n" +
            "\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform vec2 iResolution;\n" +
            "uniform float iBlurRadius; // Radius of the blur\n" +
            "uniform vec2 iBlurDirection; // Direction of the blur\n" +
            "\n" +
            "void main() {\n" +
            "  float Pi = 6.28318530718; // Pi*2\n" +
            "\n" +
            "  // GAUSSIAN BLUR SETTINGS {{{\n" +
            "  float Directions = 16.0; // BLUR DIRECTIONS (Default 16.0 - More is better but slower)\n" +
            "  float Quality = 4.0; // BLUR QUALITY (Default 4.0 - More is better but slower)\n" +
            "  float Size = iBlurRadius; // BLUR SIZE (Radius)\n" +
            "  // GAUSSIAN BLUR SETTINGS }}}\n" +
            "\n" +
            "  vec2 Radius = Size/iResolution.xy;\n" +
            "\n" +
            "  // Normalized pixel coordinates (from 0 to 1)\n" +
            "  vec2 uv = gl_FragCoord.xy/iResolution.xy;\n" +
            "  // Pixel colour\n" +
            "  vec4 color = texture(u_texture, uv);\n" +
            "\n" +
            "  // Blur calculations\n" +
            "  for( float d=0.0; d<Pi; d+=Pi/Directions)\n" +
            "  {\n" +
            "    for(float i=1.0/Quality; i<=1.0; i+=1.0/Quality)\n" +
            "    {\n" +
            "      color += texture2D(u_texture, uv+vec2(cos(d),sin(d))*Radius*i);\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  // Gamma correction\n" +
            "  float Gamma = 1.01;\n" +
            "  color.rgba = pow(color.rgba, vec4(1.0/Gamma));\n" +
            "\n" +
            "  // Output to screen\n" +
            "  color /= Quality * Directions;\n" +
            "  gl_FragColor = color;\n" +
            "}\n";


    //    @Language("GLSL")
    final String GRID_FRAG =
            "varying vec2 vTexCoord;\n" +
            "varying vec4 vColor;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform vec2 iResolution;\n" +
            "uniform vec3 hexagonColor;\n" +
            "uniform float hexagonTransparency;\n" +
            "\n" +
            "float rng( in vec2 pos )\n" +
            "{\n" +
            "    return fract(sin( pos.y + pos.x*78.233 )*43758.5453)*2.0 - 1.0;\n" +
            "}\n" +
            "\n" +
            "float simplexValue1DPart(vec2 uv, float ix) {\n" +
            "    float x = uv.x - ix;\n" +
            "    float f = 1.0 - x * x;\n" +
            "    float f2 = f * f;\n" +
            "    float f3 = f * f2;\n" +
            "    return f3;\n" +
            "}\n" +
            "\n" +
            "float simplexValue1D(vec2 uv) {\n" +
            "    vec2 iuv = floor(uv);    \n" +
            "    float n = simplexValue1DPart(uv, iuv.x);\n" +
            "    n += simplexValue1DPart(uv, iuv.x + 1.0);\n" +
            "    return rng(vec2(n * 2.0 - 1.0, 0.0));\n" +
            "}\n" +
            "\n" +
            "float perlin( in float pos )\n" +
            "{\n" +
            "    // Get node values\n" +
            "    \n" +
            "    float a = rng( vec2(floor(pos), 1.0) );\n" +
            "    float b = rng( vec2(ceil( pos), 1.0) );\n" +
            "    \n" +
            "    float a_x = rng( vec2(floor(pos), 2.0) );\n" +
            "    float b_x = rng( vec2(ceil( pos), 2.0) );\n" +
            "    \n" +
            "    a += a_x*fract(pos);\n" +
            "    b += b_x*(fract(pos)-1.0);\n" +
            "    \n" +
            "    \n" +
            "    \n" +
            "    // Interpolate values\n" +
            "    \n" +
            "    return a + (b-a)*smoothstep(0.0,1.0,fract(pos));\n" +
            "}\n" +
            "\n" +
            "void main() { \n" +
            "  vec2 uv = gl_FragCoord.xy;\n" +
            "  uv /= 24.0;\n" +
            "\n" +
            "  vec4 color = texture2D(u_texture, vTexCoord);\n" +
            "  const float A = 0.0;\n" +
            "  const float B = 0.15;\n" +
            "\n" +
            "  float x = uv.x;\n" +
            "  float y = (uv.y) * (1.5 / 3.0);\n" +
            "\n" +
            "  float val = (0.5 + 0.5 * x + 0.5 * y);\n" +
            "\n" +
            "  float noise = perlin(val);\n" +
            "  if (noise > 0.1) {\n" +
            "      noise = -1.0;\n" +
            "  }\n" +
            "\n" +
            "  noise = 1.0 - (noise + 1.0) / 2.0;\n" +
            "\n" +
            "  color.rgb = vec3(1.0);\n" +
            "  color.a = color.a * (noise * (B - A)) + A;\n" +
            "\n" +
            "  gl_FragColor = color;\n" +
            "}\n";

    @ApiStatus.Experimental
    public void blurred(Runnable block) {
        blurred(true, block);
    }

    @ApiStatus.Experimental
    public void blurred(float radius, Runnable block) {
        blurred(radius, true, block);
    }

    @ApiStatus.Experimental
    public void blurred(boolean grid, Runnable block) {
        blurred(grid, 1, block);
    }

    @ApiStatus.Experimental
    public void blurred(float radius, boolean grid, Runnable block) {
        blurred(radius, grid, 1, block);
    }

    @ApiStatus.Experimental
    public void blurred(boolean grid, int guiScale, Runnable block) {
        blurred(Config.blurRadius, grid, guiScale, block);
    }

    @ApiStatus.Experimental
    public void blurred(float radius, boolean grid, int guiScale, Runnable block) {
        blurred(1.0F, radius, grid, guiScale, block);
    }

    @ApiStatus.Experimental
    public void blurred(float overlayOpacity, float radius, boolean grid, int guiScale, Runnable block) {
        if (this.blurred) {
            block.run();
            return;
        }

        this.blurred = true;

        this.toBatch();

        try {
            var blurTargetA = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
            var blurTargetB = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
            var fboRegion = new TextureRegion(blurTargetA.getColorBufferTexture());

            //Start rendering to an offscreen color buffer
            blurTargetA.begin();

            //before rendering, ensure we are using the default shader
            this.batch.setShader(null);

            this.batch.flush();

            //render the batch contents to the offscreen buffer
            this.flush();

            block.run();

            this.toBatch();

            //finish rendering to the offscreen buffer
            this.batch.flush();

            //finish rendering to the offscreen buffer
            blurTargetA.end();

            //now let's start blurring the offscreen image
            this.batch.setShader(blurShader);

            //since we never called batch.end(), we should still be drawing
            //which means are blurShader should now be in use

            // set the shader uniforms
            blurShader.setUniformf("iBlurDirection", 1f, 0f);
            blurShader.setUniformf("iResolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            blurShader.setUniformf("iBlurRadius", radius / guiScale);
            blurShader.setUniformf("iTime", System.currentTimeMillis() / 1000.0f);

            //our first blur pass goes to target B
            blurTargetB.begin();

            //we want to render FBO target A into target B
            fboRegion.setTexture(blurTargetA.getColorBufferTexture());

            //draw the scene to target B with a horizontal blur effect
            this.batch.setColor(1f, 1f, 1f, overlayOpacity);
            this.batch.draw(fboRegion, 0, 0);

            //flush the batch before ending the FBO
            this.batch.flush();

            //finish rendering target B
            blurTargetB.end();

            //now we can render to the screen using the vertical blur shader

            //update the blur only along Y-axis
            blurShader.setUniformf("iBlurDirection", 0f, 1f);

            //update the resolution of the blur along Y-axis
            blurShader.setUniformf("iResolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            //update the Y-axis blur radius
            blurShader.setUniformf("radius", radius);

            //draw target B to the screen with a vertical blur effect
            fboRegion.setTexture(blurTargetB.getColorBufferTexture());
            this.batch.setColor(1f, 1f, 1f, overlayOpacity);
            this.batch.draw(fboRegion, 0, 0);

            //reset to default shader without blurs
            this.batch.setShader(null);

            this.flush();

            if (grid) {
                //getConfig the texture for the hexagon grid
                var colorBufferTexture = this.grid.getColorBufferTexture();

                //render the grid to the screen
                this.batch.setColor(1, 1, 1, 1);
                blurred(32, false, 1, () -> {
                    this.batch.setColor(1f, 1f, 1f, overlayOpacity);
                    this.batch.draw(colorBufferTexture, 0, 0, (float) Gdx.graphics.getWidth() / guiScale, (float) Gdx.graphics.getHeight() / guiScale);
                });
            }

            //dispose of the FBOs
            blurTargetA.dispose();
            blurTargetB.dispose();
        } finally {
            this.blurred = false;
        }
    }

    public void blurred(Texture texture) {
        if (this.blurred) {
            return;
        }

        this.vfxManager.useAsInput(texture);
        this.vfxManager.applyEffects();
        this.vfxManager.renderToScreen(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.flush();
    }

    public void resetGrid() {
        this.resizeGrid(this.width, this.height);
    }

    private void resizeGrid(int width, int height) {
        if (width == 0 || height == 0) return;

        if (this.grid != null) this.grid.dispose();

        this.grid = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        this.grid.begin();
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Color hexagonColor;
        try {
            var hexagonColorHex = Config.hexagonColor;
            if (hexagonColorHex == null) hexagonColorHex = "#ffffff";
            if (hexagonColorHex.length() > 7) hexagonColorHex = hexagonColorHex.substring(0, 7);
            if (hexagonColorHex.length() < 7 && hexagonColorHex.length() > 4)
                hexagonColorHex = hexagonColorHex.substring(0, 4);
            if (hexagonColorHex.length() < 4) hexagonColorHex = "#ffffff";
            hexagonColor = Color.hex(hexagonColorHex);
        } catch (dev.ultreon.bubbles.util.exceptions.InvalidValueException e) {
            hexagonColor = Color.WHITE;
        }

        var hexagonTransparency = Config.hexagonTransparency;

        this.batch.begin();
        this.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.batch.setShader(this.gridShader);
        this.gridShader.setUniformf("iResolution", width, height);

        this.batch.draw(this.white, 0, 0, width, height);

        this.batch.setShader(null);
        this.batch.end();
        this.grid.end();
    }

    public void renderToScreen(VfxFrameBuffer fbo) {
        this.toBatch();
        this.batch.draw(fbo.getTexture(), 0, 0, this.getWidth(), this.getHeight());
        this.flush();
    }

    public void arcLine(float x, float y, float radius, float startAngle, float angle, float lineWidth) {
        this.arcLine(x, y, radius, startAngle, angle, lineWidth, this.sides.estimateSidesRequired(1.0f, radius, radius));
    }

    public void arcLine(float x, float y, float radius, float startAngle, float angle, float lineWidth, int segments) {
        this.toShapes();
        this.shapes.set(Line);

        var endAngle = startAngle + angle;

        var vertices = new float[segments * 2];

        for (var i = 0; i < segments; i++) {
            var angleNow = MathUtils.lerp(startAngle, endAngle, (float) i / (segments - 1));
            var startX = x + radius * MathUtils.cosDeg(angleNow);
            var startY = y + radius * MathUtils.sinDeg(angleNow);

            vertices[i * 2] = startX;
            vertices[i * 2 + 1] = startY;
        }

        this.shapes.polyline(vertices);
        this.shapes.flush();
    }

    public void roundRect(float x, float y, float width, float height, float radius) {
        if (!this.rendering) return;

        this.toShapes();
        this.shapes.set(Line);

        x += this.lineThickness / 2f;
        y += this.lineThickness / 2f;
        width -= this.lineThickness;
        height -= this.lineThickness;
        radius -= this.lineThickness / 2;

        var cornerRadius = Math.min(radius, Math.min(width, height) / 2 - 4);

        Gdx.gl20.glLineWidth(this.lineThickness);
        this.shapes.line(x + cornerRadius, y, x + width - cornerRadius, y); // Draw top line
        this.shapes.line(x + cornerRadius, y + height, x + width - cornerRadius, y + height); // Draw bottom line
        this.shapes.line(x, y + cornerRadius, x, y + height - cornerRadius); // Draw left line
        this.shapes.line(x + width, y + cornerRadius, x + width, y + height - cornerRadius); // Draw right line

        this.arcLine(x + cornerRadius, y + cornerRadius, cornerRadius, 180f, 90f, this.lineThickness); // Draw top-left arc
        this.arcLine(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270f, 90f, this.lineThickness); // Draw top-right arc
        this.arcLine(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90f, 90f, this.lineThickness); // Draw bottom-left arc
        this.arcLine(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 0f, 90f, this.lineThickness); // Draw bottom-right arc
    }

    public void fillRoundRect(float x, float y, float width, float height, float radius, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());

        var cornerRadius = Math.min(radius, Math.min(width, height) / 2 - 4);

        // Draw the top-left rounded corner
        this.shapes.arc(x + cornerRadius, y + cornerRadius, cornerRadius, 180f, 91f, this.sides.estimateSidesRequired(1.0f, cornerRadius, cornerRadius));

        // Draw the top-right rounded corner
        this.shapes.arc(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270f, 91f, this.sides.estimateSidesRequired(1.0f, cornerRadius, cornerRadius));

        // Draw the bottom-right rounded corner
        this.shapes.arc(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 0f, 91f, this.sides.estimateSidesRequired(1.0f, cornerRadius, cornerRadius));

        // Draw the bottom-left rounded corner
        this.shapes.arc(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90f, 91f, this.sides.estimateSidesRequired(1.0f, cornerRadius, cornerRadius));

        // Draw the top and bottom straight sides
        this.shapes.rect(x + cornerRadius, y, width - 2 * cornerRadius, height);

        // Draw the left and right straight sides
        this.shapes.rect(x, y + cornerRadius, width, height - 2 * cornerRadius);

    }

    public void ellipse(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(this.color.toGdx());
        this.shapes.ellipse(x, y, width, height);
    }

    public void ellipse(int x, int y, int width, int height, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(color.toGdx());
        this.shapes.ellipse(x, y, width, height);
    }

    public void ellipse(float x, float y, float width, float height, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(color.toGdx());
        this.shapes.ellipse(x, y, width, height);
    }

    public void fillEllipse(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(this.color.toGdx());
        this.shapes.ellipse(x, y, width, height);
    }

    public void fillEllipse(int x, int y, int width, int height, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());
        this.shapes.ellipse(x, y, width, height);
    }

    public void fillEllipse(float x, float y, float width, float height, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());
        this.shapes.ellipse(x, y, width, height);
    }

    public void arcLine(int x, int y, int width, int height, int startAngle, int arcAngle, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(color.toGdx());
        this.shapes.arc(x, y, width, startAngle, arcAngle);
    }

    public void arc(int x, int y, int width, int height, int startAngle, int arcAngle, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());
        this.shapes.arc(x, y, width, startAngle, arcAngle);
    }

    public void polygon(Polygon p, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(color.toGdx());
        this.shapes.polygon(p.getTransformedVertices());
    }

    public void fillPolygon(Polygon p, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());

        var transformedVertices = p.getTransformedVertices();
        for (var i = 0; i < transformedVertices.length - 2; i += 2) {
            var x1 = transformedVertices[i];
            var y1 = transformedVertices[i + 1];
            var x2 = transformedVertices[i + 2];
            var y2 = transformedVertices[i + 3];
            var x3 = transformedVertices[0];
            var y3 = transformedVertices[1];

            this.shapes.triangle(x1, y1, x2, y2, x3, y3);
        }
    }

    @Deprecated(forRemoval = true)
    public void fillPolyline(Polyline p) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.gl20.glLineWidth(this.lineThickness);
        this.shapes.setColor(this.color.toGdx());
        this.shapes.polyline(p.getTransformedVertices());
    }

    public void polyline(Polyline p, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(color.toGdx());
        this.shapes.polyline(p.getTransformedVertices());
    }

    public void polyline(float[] vertices, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setLine();
        this.shapes.setColor(color.toGdx());
        this.shapes.polyline(vertices);
    }

    public void fillPolyline(Polyline p, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(color.toGdx());
        this.shapes.polyline(p.getTransformedVertices());
    }

    public void blit(Texture tex, float x, float y) {
        if (!this.rendering) return;

        this.toBatch();
        this.batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, Color backgroundColor) {
        if (!this.rendering) return;

        if (tex == null) {
            this.batch.draw(TextureManager.DEFAULT_TEX, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
            return;
        }

        this.toShapes();
        this.fill(x, y, tex.getWidth(), tex.getHeight(), backgroundColor);
        this.toBatch();
        this.batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.toBatch();

        if (tex == null) {
            this.batch.draw(TextureManager.DEFAULT_TEX, x, y + height, width, -height);
            return;
        }

        this.batch.draw(tex, x, y + height, width, -height);
    }

    public void blit(Texture tex, float x, float y, float width, float height, Color backgroundColor) {
        if (!this.rendering) return;

        this.fill(x, y, width, height, backgroundColor);

        this.toBatch();
        this.batch.draw(tex, x, y + height, width, -height);
    }

    public void drawText(BitmapFont font, String str, int x, int y, Color color) {
        if (!this.rendering) return;

        this.toBatch();
        font.setColor(color.toGdx());
        font.draw(this.batch, str, x, y);
    }

    public void drawText(BitmapFont font, String str, float x, float y, Color color) {
        if (!this.rendering) return;

        this.toBatch();
        font.setColor(color.toGdx());
        font.draw(this.batch, str, x, y);
    }

    public void drawText(BitmapFont font, TextObject str, int x, int y, Color color) {
        if (!this.rendering) return;

        this.toBatch();
        font.setColor(color.toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    public void drawText(BitmapFont font, TextObject str, float x, float y, Color color) {
        if (!this.rendering) return;

        this.toBatch();
        font.setColor(color.toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    public void drawText(String str, float x, float y, Anchor anchor, Color color) {
        if (!this.rendering) return;


        var layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(this.font, str);

        var anchoredX = (anchor.getX() + 1) * (layout.width / 2);
        var anchoredY = (anchor.getY() + 1) * (layout.height / 2);
        this.toBatch();
        this.font.setColor(color.toGdx());
        this.font.draw(this.batch, str, x, y);
    }

    public void drawMultiLineText(BitmapFont font, String str, int x, int y, Color color) {
        if (!this.rendering) return;

        for (var line : str.split("\n"))
            this.drawText(font, line, x, y += (int) font.getLineHeight(), color);
    }

    public void drawWrappedText(BitmapFont font, String text, int x, int y, int maxWidth, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        var lines = StringUtils.wrap(text, font, new GlyphLayout(), maxWidth);
        var joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(font, joined, x, y, color);
    }

    public void drawWrappedText(BitmapFont font, TextObject text, int x, int y, int maxWidth, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        var lines = StringUtils.wrap(text.getText(), font, new GlyphLayout(), maxWidth);
        var joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(font, joined, x, y, color);
    }

    public void drawTabbedText(BitmapFont font, String text, int x, int y, Color color) {
        if (!this.rendering) return;

        for (var line : text.split("\t"))
            this.drawText(font, line, x += (int) font.getLineHeight(), y, color);
    }

    public void drawTextCenter(BitmapFont font, String text, float x, float y, Color color) {
        if (!this.rendering) return;

        this.layout.setText(font, text);

        // Determine the X coordinate for the text
        x -= this.layout.width / 2;

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        y -= (this.layout.height + font.getDescent()) / 2;

        // Draw the String
        this.drawText(font, text, x, y, color);
    }

    public void drawTextCenter(BitmapFont font, TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        var string = text.getText();

        this.layout.setText(font, string);

        // Determine the X coordinate for the text
        x -= this.layout.width / 2;

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        y -= (this.layout.height + font.getDescent()) / 2;

        // Draw the String
        this.drawText(font, string, x, y, color);
    }

    public void drawTextLeft(BitmapFont font, String text, float x, float y, Color color) {
        if (!this.rendering) return;

        this.layout.setText(font, text);

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        y -= (this.layout.height + font.getDescent()) / 2;

        // Draw the String
        this.drawText(font, text, x, y, color);
    }

    public void drawTextLeft(BitmapFont font, TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        var string = text.getText();

        this.layout.setText(font, string);

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        y -= (this.layout.height + font.getDescent()) / 2;

        // Draw the String
        this.drawText(font, string, x, y, color);
    }

    public void drawTextRight(BitmapFont font, String text, float x, float y, Color color) {
        if (!this.rendering) return;

        this.layout.setText(font, text);

        // Determine the X coordinate for the text
        x -= this.layout.width;

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        y -= (this.layout.height + font.getDescent()) / 2;

        // Draw the String
        this.drawText(font, text, x, y, color);
    }

    public void drawTextRight(BitmapFont font, TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        var string = text.getText();

        this.layout.setText(font, string);

        // Determine the X coordinate for the text
        x -= this.layout.width;

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        y -= (this.layout.height + font.getDescent()) / 2;

        // Draw the String
        this.drawText(font, string, x, y, color);
    }

    public void clear() {
        if (!this.rendering) return;

        // Clean up the screen.
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Clean up internal buffers, as we don't need any information from the last render.
        this.vfxManager.cleanUpBuffers(Color.BLACK.toGdx());

        ScreenUtils.clear(this.clearColor.toGdx(), true);
    }

    ////////////////////////////
    //     Transformation     //
    ////////////////////////////
    private void editMatrix(Function<Matrix4, Matrix4> editor) {
        this.flush();
        var matrix = editor.apply(this.shapes.getProjectionMatrix());
        editor.apply(this.globalTransform);
        this.shapes.setProjectionMatrix(matrix);
        this.batch.setProjectionMatrix(matrix);

        this.matrixStack.last().set(matrix);
        this.flush();
    }

    public void translate(float x, float y) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> matrix.translate(x, y, 0));
    }

    public void translate(int x, int y) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> matrix.translate(x, y, 0));
    }

    public void translate(float x, float y, float z) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> matrix.translate(x, y, z));
    }

    public void translate(int x, int y, int z) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> matrix.translate(x, y, z));
    }

    public void rotate(float theta) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> matrix.rotate(theta, 1, 0, 0));
    }

    public void scale(float width, float height, float depth) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> matrix.scale(width, height, 0));
    }

    public void scale(float width, float height) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> matrix.scale(width, height, 0));
    }

    @CheckReturnValue
    @ApiStatus.Internal
    private boolean pushScissor(Rectangle rect) {
        if (!this.rendering) return false;

        if (BubbleBlasterConfig.DEBUG_DISABLE_SCISSORS.get()) return true;

        rect.getPosition(this.tmp2A);
        this.globalTransform.getTranslation(this.tmp3A);
        rect.setPosition(this.tmp2A.add(this.tmp3A.x, this.tmp3A.y));

        if (rect.x < 0) {
            rect.width = Math.max(rect.width + rect.x, 0);
            rect.x = 0;
        }

        if (rect.y < 0) {
            rect.height = Math.max(rect.height + rect.y, 0);
            rect.y = 0;
        }

        if (rect.width < 1) return false;
        if (rect.height < 1) return false;

        rect.y = Gdx.graphics.getHeight() - rect.y - rect.height;

        if (!BubbleBlasterConfig.DEBUG_DISABLE_SCISSORS.get()) {
            this.flush();
            if (!ScissorStack.pushScissors(rect)) {
                if (this.loggingScissors) {
                    Debug.log("ScissorDebug", String.format("Scissor [%d]: %s", this.scissorDepth, rect));
                }
                return false;
            }

            if (this.loggingScissors) {
                this.scissorDepth++;
                Debug.log("ScissorDebug", String.format("Pushing scissor [%d]: %s", this.scissorDepth, rect));
            }
            return true;
        }
        return false;
    }

    public void flush() {
        switch (this.state) {
            case BATCH:
                this.batch.flush();
                break;
            case SHAPES:
                this.shapes.flush();
                break;
        }

        Gdx.gl.glFlush();
    }

    public void triggerScissorLog() {
        this.triggerScissorLog = true;
    }

    @CheckReturnValue
    @ApiStatus.Internal
    private boolean pushScissor(float x, float y, float width, float height) {
        if (!this.rendering) return false;

        return this.pushScissor(new Rectangle(x, y, width, height));
    }

    @CheckReturnValue
    @ApiStatus.Internal
    private boolean pushScissor(int x, int y, int width, int height) {
        if (!this.rendering) return false;

        return this.pushScissor(new Rectangle(x, y, width, height));
    }

    @CanIgnoreReturnValue
    @ApiStatus.Internal
    private Rectangle popScissor() {
        if (!this.rendering) return null;

        if (BubbleBlasterConfig.DEBUG_DISABLE_SCISSORS.get()) return new Rectangle();

        if (!BubbleBlasterConfig.DEBUG_DISABLE_SCISSORS.getOrDefault()) {
            this.flush();
            var rectangle = ScissorStack.popScissors();

            if (this.loggingScissors) {
                Debug.log("ScissorDebug", String.format("Popping scissor [%d]", this.scissorDepth));
                this.scissorDepth--;
            }
            return rectangle;
        }
        return this.game.getBounds();
    }

    public void clearScissors() {
        if (!this.rendering) return;

        this.flush();
        while (ScissorStack.peekScissors() != null) {
            ScissorStack.popScissors();
        }
        this.flush();
    }

    @ApiStatus.Experimental
    public void clearMatrixStack() {
        if (!this.rendering) return;

        while (this.matrixStack.last() != null && !this.matrixStack.isClear()) {
            this.popMatrix();
        }
    }

    @ApiStatus.Experimental
    public void pushMatrix() {
        if (!this.rendering) return;

        this.matrixStack.push();
        this.globalMatrixStack.push();
    }

    @ApiStatus.Experimental
    public void popMatrix() {
        if (!this.rendering) return;

        if (this.matrixStack.isClear()) throw new IllegalStateException("Matrix stack is already clear");

        this.flush();
        this.matrixStack.pop();
        this.camera.combined.set(this.matrixStack.last());
        this.globalTransform.set(this.globalMatrixStack.last());
        this.flush();
    }

    public boolean hitClip(int x, int y, int width, int height) {
        return false;
    }

    public void drawEffectBox(Rectangle bounds) {
        if (!this.rendering) return;

        this.drawEffectBox(bounds.x, bounds.y, bounds.width, bounds.height, new Insets(2));
    }

    public void drawEffectBox(float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.drawEffectBox(x, y, width, height, new Insets(2));
    }

    public void drawEffectBox(float x, float y, float width, float height, Insets insets) {
        if (!this.rendering) return;

        this.drawEffectBox(x, y, width, height, insets, BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.getOrDefault());
    }

    public void drawEffectBox(Rectangle bounds, Insets insets) {
        if (!this.rendering) return;

        this.drawEffectBox(bounds.x, bounds.y, bounds.width, bounds.height, insets, BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.getOrDefault());
    }

    public void drawEffectBox(float x, float y, float width, float height, Insets insets, float speed) {
        if (!this.rendering) return;

        var border = new Border(insets);
        border.setRenderType(Border.RenderType.EFFECT);
        border.setEffectSpeed(speed);
        border.drawBorder(this, x, y, width, height);
    }

    public void drawRoundEffectBox(float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.drawRoundEffectBox(x, y, width, height, BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.getOrDefault());
    }

    public void drawRoundEffectBox(float x, float y, float width, float height, float radius) {
        if (!this.rendering) return;

        this.drawRoundEffectBox(x, y, width, height, radius, 5);
    }

    public void drawRoundEffectBox(float x, float y, float width, float height, float radius, int borderWidth) {
        if (!this.rendering) return;

        this.drawRoundEffectBox(x, y, width, height, radius, borderWidth, BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.getOrDefault());
    }

    public void drawRoundEffectBox(float x, float y, float width, float height, float radius, int lineWidth, float speed) {
        if (!this.rendering) return;

        this.setLineThickness(lineWidth);
        this.withEffect(() -> this.roundRect(x, y, width, height, radius));
    }

    public void drawErrorEffectBox(Rectangle bounds) {
        this.drawErrorEffectBox((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
    }

    public void drawErrorEffectBox(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.drawErrorEffectBox(x, y, width, height, new Insets(2));
    }

    public void drawErrorEffectBox(Rectangle bounds, Insets insets) {
        this.drawErrorEffectBox((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, insets);
    }

    public void drawErrorEffectBox(int x, int y, int width, int height, Insets insets) {
        if (!this.rendering) return;

        this.drawErrorEffectBox(x, y, width, height, insets, BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.getOrDefault());
    }

    public void drawErrorEffectBox(Rectangle bounds, Insets insets, float speed) {
        this.drawErrorEffectBox((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, insets, speed);
    }

    public void drawErrorEffectBox(int x, int y, int width, int height, Insets insets, float speed) {
        if (!this.rendering) return;

        var border = new Border(insets);
        border.setRenderType(Border.RenderType.ERROR_EFFECT);
        border.setEffectSpeed(speed);
        border.drawBorder(this, x, y, width, height);
    }

    public void fillErrorEffect(float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.fillScrollingGradient(x, y, width, height, BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.getOrDefault(), ANIM_ERROR_COLOR_1, ANIM_ERROR_COLOR_2);
    }

    public void fillErrorEffect(float x, float y, float width, float height, float speed) {
        if (!this.rendering) return;

        this.fillScrollingGradient(x, y, width, height, speed, ANIM_ERROR_COLOR_1, ANIM_ERROR_COLOR_2);
    }

    public void fillEffect(float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.fillScrollingGradient(x, y, width, height, BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.getOrDefault(), ANIM_COLOR_1, ANIM_COLOR_2);
    }

    public void drawEffectBox(Rectangle bounds, Insets insets, float speed) {
        this.drawEffectBox((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, insets, speed);
    }

    public void fillEffect(float x, float y, float width, float height, float speed) {
        if (!this.rendering) return;

        this.fillScrollingGradient(x, y, width, height, speed, ANIM_COLOR_1, ANIM_COLOR_2);
    }

    private void fillScrollingGradient(float x, float y, float width, float height, float speed, Color color1, Color color2) {
        float gameWidth = this.getWidth();
        float gameHeight = this.getHeight();
        var shiftX = (gameWidth * 2f * BubbleBlaster.getTicks() / (BubbleBlaster.TPS * speed) - this.camera.combined.getTranslation(new Vector3()).x) % (gameWidth * 2);

        this.scissored(new Rectangle(x, y, width, height), () -> {
            this.fillGradient(-shiftX, 0, gameWidth, gameHeight, color1, color2, Axis2D.HORIZONTAL);
            this.fillGradient(-shiftX + gameWidth, 0, gameWidth, gameHeight, color2, color1, Axis2D.HORIZONTAL);
            this.fillGradient(-shiftX + gameWidth * 2, 0, gameWidth, gameHeight, color1, color2, Axis2D.HORIZONTAL);
        });
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    public void blit(int x, int y) {
        if (!this.rendering) return;

        var tex = this.texture;

        this.toBatch();
        this.batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.toBatch();
        this.batch.draw(this.texture, x, y + height, width, -height);
    }

    public void setTexture(Identifier texture) {
        if (!this.rendering) return;

        this.texture = this.game.getTextureManager().getTexture(texture);
    }

    @ApiStatus.Experimental
    public void setTexture(Texture texture) {
        if (!this.rendering) return;

        this.texture = texture;
    }

    ////////////////////////
    //     Properties     //
    ////////////////////////
    @ApiStatus.Experimental
    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    @ApiStatus.Experimental
    public Matrix4 getTransform() {
        return this.shapes.getTransformMatrix();
    }

    @ApiStatus.Experimental
    public void setTransform(Matrix4 matrix) {
        if (!this.rendering) return;

        var m = this.matrixStack.last();
        if (m != null) {
            m.set(matrix);
            this.batch.setTransformMatrix(m);
        }
    }

    public void setLineThickness(float lineThickness) {
        if (!this.rendering) return;

        this.lineThickness = lineThickness;
    }

    public float getLineThickness() {
        return this.lineThickness;
    }

    public void setColor(Color c) {
        if (!this.rendering) return;
        if (c == null) return;

        this.font.setColor(c.toGdx());
        this.shapes.setColor(c.toGdx());
    }

    public void setColor(int r, int g, int b) {
        if (!this.rendering) return;

        this.setColor(Color.rgb(r, g, b));
    }

    public void setColor(float r, float g, float b) {
        if (!this.rendering) return;

        this.setColor(Color.rgb(r, g, b));
    }

    public void setColor(int r, int g, int b, int a) {
        if (!this.rendering) return;

        this.setColor(Color.rgba(r, g, b, a));
    }

    public void setColor(float r, float g, float b, float a) {
        if (!this.rendering) return;

        this.setColor(Color.rgba(r, g, b, a));
    }

    public void setColor(int argb) {
        if (!this.rendering) return;

        this.setColor(Color.argb(argb));
    }

    /**
     * Sets current color from a color hex.
     * Examples:
     * <code>
     * color("#f70")
     * color("#fff7")
     * color("#ffd500")
     * color("#aab70077")
     * </code>
     *
     * @param hex a color hex.
     */
    public void setColor(String hex) {
        if (!this.rendering) return;

        this.setColor(Color.hex(hex));
    }

    public Color getShapesColor() {
        return Color.gdx(this.shapes.getColor());
    }

    public void setClearColor(Color color) {
        if (!this.rendering) return;

        this.gl20.glClearColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public void setClearColor(int red, int green, int blue) {
        if (!this.rendering) return;

        this.setClearColor(Color.rgb(red, green, blue));
    }

    public void setClearColor(float red, float green, float blue) {
        if (!this.rendering) return;

        this.setClearColor(Color.rgb(red, green, blue));
    }

    public void setClearColor(int red, int green, int blue, int alpha) {
        if (!this.rendering) return;

        this.setClearColor(Color.rgba(red, green, blue, alpha));
    }

    public void setClearColor(float red, float green, float blue, float alpha) {
        if (!this.rendering) return;

        this.setClearColor(Color.rgba(red, green, blue, alpha));
    }

    public void setClearColor(int argb) {
        if (!this.rendering) return;

        this.setClearColor(Color.argb(argb));
    }

    public void setClearColor(String hex) {
        if (!this.rendering) return;

        this.setClearColor(Color.hex(hex));
    }

    public Color getClearColor() {
        return this.clearColor;
    }

    public void setFont(BitmapFont font) {
        if (!this.rendering) return;

        this.font = font;
    }

    public BitmapFont getFont() {
        return this.font;
    }

    public void roundedLine(float x1, float y1, float x2, float y2) {
//        this.shapes.path(Array.with(new Vector2(x1, y1), new Vector2(x2, y2)), this.lineWidth, JoinType.SMOOTH, false);
    }

    public void setPixel(Pixel pixel) {
        this.toShapes();
        this.setPoint();
        this.shapes.setColor(Color.rgba(pixel.getColor().getRed(), pixel.getColor().getGreen(), pixel.getColor().getBlue(), pixel.getColor().getAlpha()).toGdx());
        this.shapes.rect(pixel.getX(), pixel.getY(), 1, 1);
    }

    private void setPoint() {
        this.shapes.set(Point);
    }

    private void setLine() {
        this.gl20.glLineWidth(this.lineThickness);
        this.shapes.set(Line);
    }

    private void setFilled() {
        this.shapes.set(Filled);
    }

    @Override
    public String toString() {
        return "Renderer{" +
                "rendering=" + this.rendering +
                '}';
    }

    public void scissored(Rectangle rect, Runnable func) {
        if (this.pushScissor(rect)) {
            func.run();
            this.popScissor();
        }
    }

    public void scissored(float x, float y, float width, float height, Runnable func) {
        if (this.pushScissor(x, y, width, height)) {
            func.run();
            this.popScissor();
        }
    }

    public void scissored(int x, int y, int width, int height, Runnable func) {
        if (this.pushScissor(x, y, width, height)) {
            func.run();
            this.popScissor();
        }
    }

    public void scissored(Rectangle rect, Consumer<Rectangle> func) {
        if (this.pushScissor(rect)) {
            func.accept(ScissorStack.peekScissors());
            this.popScissor();
        }
    }

    public void scissored(float x, float y, float width, float height, Consumer<Rectangle> func) {
        if (this.pushScissor(x, y, width, height)) {
            func.accept(ScissorStack.peekScissors());
            this.popScissor();
        }
    }

    public void scissored(int x, int y, int width, int height, Consumer<Rectangle> func) {
        if (this.pushScissor(x, y, width, height)) {
            func.accept(ScissorStack.peekScissors());
            this.popScissor();
        }
    }

    private void toBatch() {
        if (!this.stateChange) return;

        if (this.state == State.BATCH) {
            return;
        }
        this.shapes.flush();
        this.shapes.end();
        this.batch.begin();
        this.state = State.BATCH;
    }

    private void toShapes() {
        if (!this.stateChange) return;

        if (this.state == State.SHAPES) {
            return;
        }
        this.batch.flush();
        this.batch.end();

        if (this.blendingEnabled && !Gdx.gl20.glIsEnabled(GL20.GL_BLEND)) {
            Gdx.gl20.glEnable(GL20.GL_BLEND);
            Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        if (this.depthEnabled && !Gdx.gl20.glIsEnabled(GL20.GL_DEPTH_TEST)) {
            Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        }

        this.shapes.begin();
        this.state = State.SHAPES;
    }

    public void hovered() {
        this.hovering = true;
    }

    public void hideCursor() {
        this.hideCursor = true;
    }

    public int getFreeFrameBuffers() {
        return this.fboPool.getFreeCount();
    }

    public int getManagedFrameBuffers() {
        return this.fboPool.getManagedCount();
    }

    public void blit(Texture texture, Rectangle bounds) {
        this.blit(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public enum State {
        BATCH, SHAPES
    }

    private class FboPool extends VfxFrameBufferPool {
        private final int capacity;

        public FboPool(Pixmap.Format format, int width, int height) {
            this(format, width, height, 16);
        }

        public FboPool(Pixmap.Format format, int width, int height, int capacity) {
            super(format, width, height, capacity);
            this.capacity = capacity;
        }

        @Override
        protected VfxFrameBuffer createBuffer() {
            if (this.managedBuffers.size >= this.getCapacity()) throw new IllegalStateException("Frame buffer pool capacity reached: " + this.managedBuffers.size);
            return super.createBuffer();
        }

        public int getCapacity() {
            return this.capacity;
        }

        public int getManagedCount() {
            return this.managedBuffers.size;
        }

        public void freeAll() {
            this.cleanupInvalid();

            var copy = new Array<>(this.managedBuffers);
            for (var fbo : copy)
                this.free(fbo);

            copy.clear(); // Todo: Check performance.

            this.clearFree();
        }
    }
}
