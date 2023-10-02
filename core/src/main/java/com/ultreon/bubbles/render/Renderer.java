/////////////////////
//     Package     //
/////////////////////
package com.ultreon.bubbles.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.ultreon.bubbles.Axis2D;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.debug.Debug;
import com.ultreon.bubbles.render.gui.border.Border;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.commons.util.StringUtils;
import com.ultreon.libs.commons.v0.Anchor;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Pixel;
import com.ultreon.libs.text.v1.TextObject;
import org.jetbrains.annotations.ApiStatus;
import space.earlygrey.shapedrawer.DefaultSideEstimator;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.List;
import java.util.UUID;
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
@SuppressWarnings({"unused", "FieldCanBeLocal"})
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
    private boolean blendingEnabled;

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
        this.matrixStack.onEdit = camera.combined::set;
    }

    @ApiStatus.Internal
    public void begin() {
        if (this.rendering)
            throw new IllegalStateException("Renderer is already rendering");

        this.backupMatrix.set(this.camera.combined);
        this.matrixStack.stack.addLast(this.camera.combined);

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
//        this.enableDepthTest();
    }

    @ApiStatus.Internal
    public void end() {
        if (!this.rendering)
            throw new IllegalStateException("Renderer isn't rendering yet");

        this.camera.combined.set(this.backupMatrix);

        if (!this.matrixStack.isClear())
            this.clearMatrixStack();

        this.matrixStack.stack.removeLast();

//        this.disableDepthTest();
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

        this.rendering = false;
    }

    public void enableBlend() {
        this.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.blendingEnabled = true;
    }

    private void enableDepthTest() {
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
    }

    public void disableBlend() {
        this.blendingEnabled = false;
        Gdx.gl20.glDisable(GL20.GL_BLEND);
    }

    public void disableDepthTest() {
        Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);
    }

    public void outline(Shape2D shape, Color color) {
        if (shape instanceof Circle circle) this.outline(circle, color);
        else if (shape instanceof Rectangle rectangle) this.outline(rectangle, color);
        else if (shape instanceof Polygon polygon) this.outline(polygon, color);
        else if (shape instanceof Ellipse ellipse) this.outline(ellipse, color);
        else throw new UnsupportedOperationException("Shape not supported: " + shape.getClass().getName());
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

        if (s instanceof Circle circle) this.fill(circle, color);
        else if (s instanceof Ellipse ellipse) this.fill(ellipse, color);
        else if (s instanceof Rectangle rect) this.fill(rect, color);
        else if (s instanceof Polygon rect) this.fill(rect, color);
        else if (s instanceof Polyline rect) this.fill(rect, color);
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

    public void withEffect(Runnable func) {
        this.toShapes();
        this.flush();
        this.disableStateChange();
//        this.disableDepthTest();
        this.drawMasks(func);
        this.drawMasked();
//        this.enableDepthTest();
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
            case HORIZONTAL -> this.shapes.rect(x, y, width, height, color2.toGdx(), color1.toGdx(), color1.toGdx(), color2.toGdx());
            case VERTICAL -> this.shapes.rect(x, y, width, height, color1.toGdx(), color1.toGdx(), color2.toGdx(), color2.toGdx());
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

        Border border = new Border(1);
        border.setColor(color);
        border.drawBorder(this, x, y, width, height);
    }

    public void box(int x, int y, int width, int height, Color color, Insets insets) {
        if (!this.rendering) return;

        Border border = new Border(insets);
        border.setColor(color);
        border.drawBorder(this, x, y, width, height);
    }

    public void box(float x, float y, float width, float height, Color color, Insets insets) {
        if (!this.rendering) return;

        Border border = new Border(insets);
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

    public void arcLine(float x, float y, float radius, float startAngle, float angle, float lineWidth) {
        this.arcLine(x, y, radius, startAngle, angle, lineWidth, this.sides.estimateSidesRequired(1.0f, radius, radius));
    }

    public void arcLine(float x, float y, float radius, float startAngle, float angle, float lineWidth, int segments) {
        this.toShapes();
        this.shapes.set(Line);

        var endAngle = startAngle + angle;

        float[] vertices = new float[segments * 2];

        for (int i = 0; i < segments; i++) {
            float angleNow = MathUtils.lerp(startAngle, endAngle, (float) i / (segments - 1));
            float startX = x + radius * MathUtils.cosDeg(angleNow);
            float startY = y + radius * MathUtils.sinDeg(angleNow);

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

        float[] transformedVertices = p.getTransformedVertices();
        for (int i = 0; i < transformedVertices.length - 2; i += 2) {
            float x1 = transformedVertices[i];
            float y1 = transformedVertices[i + 1];
            float x2 = transformedVertices[i + 2];
            float y2 = transformedVertices[i + 3];
            float x3 = transformedVertices[0];
            float y3 = transformedVertices[1];

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
        this.shapes.setColor(this.color.toGdx());
        this.shapes.polyline(p.getTransformedVertices());
    }

    public void fillPolyline(Polyline p, Color color) {
        if (!this.rendering) return;

        this.toShapes();
        this.setFilled();
        this.shapes.setColor(this.color.toGdx());
        this.shapes.polyline(p.getTransformedVertices());
    }

    public void blit(Texture tex, float x, float y) {
        if (!this.rendering) return;

        this.toBatch();
        this.batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, Color backgroundColor) {
        if (!this.rendering) return;

        this.fill(x, y, tex.getWidth(), tex.getHeight(), backgroundColor);

        this.toBatch();
        this.batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.toBatch();
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

    @ApiStatus.Experimental
    public void drawText(BitmapFont font, TextObject str, int x, int y, Color color) {
        if (!this.rendering) return;

        this.toBatch();
        font.setColor(color.toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    @ApiStatus.Experimental
    public void drawText(BitmapFont font, TextObject str, float x, float y, Color color) {
        if (!this.rendering) return;

        this.toBatch();
        font.setColor(color.toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    public void drawText(String str, float x, float y, Anchor anchor, Color color) {
        if (!this.rendering) return;


        GlyphLayout layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(this.font, str);

        float anchoredX = (anchor.getX() + 1) * (layout.width / 2);
        float anchoredY = (anchor.getY() + 1) * (layout.height / 2);
        this.toBatch();
        this.font.setColor(color.toGdx());
        this.font.draw(this.batch, str, x, y);
    }

    public void drawMultiLineText(BitmapFont font, String str, int x, int y, Color color) {
        if (!this.rendering) return;

        for (String line : str.split("\n"))
            this.drawText(font, line, x, y += (int) font.getLineHeight(), color);
    }

    public void drawWrappedText(BitmapFont font, String text, int x, int y, int maxWidth, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        List<String> lines = StringUtils.wrap(text, font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(font, joined, x, y, color);
    }

    public void drawWrappedText(BitmapFont font, TextObject text, int x, int y, int maxWidth, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        List<String> lines = StringUtils.wrap(text.getText(), font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(font, joined, x, y, color);
    }

    public void drawTabbedText(BitmapFont font, String text, int x, int y, Color color) {
        if (!this.rendering) return;

        for (String line : text.split("\t"))
            this.drawText(font, line, x += (int) font.getLineHeight(), y, color);
    }

    public void drawTextCenter(BitmapFont font, String text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawCenteredString(this, text, new Vector2(x, y), font, color);
    }

    public void drawTextCenter(BitmapFont font, TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawCenteredString(this, text, new Vector2(x, y), font, color);
    }

    public void drawTextLeft(BitmapFont font, TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text.getText(), new Vector2(x, y), font, color);
    }

    public void drawTextLeft(BitmapFont font, String text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text, new Vector2(x, y), font, color);
    }

    public void drawTextRight(BitmapFont font, String text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text, new Vector2(x, y), font, color);
    }

    public void drawTextRight(BitmapFont font, TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text.getText(), new Vector2(x, y), font, color);
    }

    public void clear() {
        if (!this.rendering) return;

        ScreenUtils.clear(this.clearColor.toGdx(), true);
    }

    ////////////////////////////
    //     Transformation     //
    ////////////////////////////
    private void editMatrix(Function<Matrix4, Matrix4> editor) {
//        this.flush();
//        Matrix4 matrix = editor.apply(this.camera.combined);
//        editor.apply(this.globalTransform);
//        this.matrixStack.last().set(this.camera.combined.set(matrix));
//        this.flush();
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

    @Deprecated
    public void subInstance(int x, int y, int width, int height, Consumer<Renderer> consumer) {
        if (!this.rendering) return;

//        this.pushMatrix();
//        this.translate(x, y);
        this.scissored(x, y, width, height, () -> {
            if (BubbleBlasterConfig.DEBUG_LOG_EMPTY_SCISSORS.getOrDefault()) {
                String formatted = "Empty Scissor @ %d, %d".formatted(x, y);
                BubbleBlaster.LOGGER.warn(formatted);
                Debug.notifyOnce(UUID.fromString("b3d5760c-fbf6-4d61-b64c-0c0ef9d7f1a3"), "Scissor Debug", formatted);
            }
        });
//        consumer.accept(this);
//        this.popMatrix();
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

        if (!BubbleBlasterConfig.DEBUG_DISABLE_SCISSORS.getOrDefault()) {
            this.flush();
            if (!ScissorStack.pushScissors(rect)) {
                if (this.loggingScissors) {
                    Debug.log("ScissorDebug", "Scissor [%d]: %s".formatted(this.scissorDepth, rect));
                }
                return false;
            }

            if (this.loggingScissors) {
                this.scissorDepth++;
                Debug.log("ScissorDebug", "Pushing scissor [%d]: %s".formatted(this.scissorDepth, rect));
            }
            return true;
        }
        return false;
    }

    public void flush() {
        switch (this.state) {
            case BATCH -> this.batch.flush();
            case SHAPES -> this.shapes.flush();
        }
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
            Rectangle rectangle = ScissorStack.popScissors();

            if (this.loggingScissors) {
                Debug.log("ScissorDebug", "Popping scissor [%d]".formatted(this.scissorDepth));
                this.scissorDepth--;
            }
            return rectangle;
        }
        return this.game.getBounds();
    }

    @ApiStatus.Experimental
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

    public void pushMatrix() {
        if (!this.rendering) return;

        this.matrixStack.push();
        this.globalMatrixStack.push();
    }

    public void popMatrix() {
//        if (!this.rendering) return;
//
//        if (this.matrixStack.isClear()) throw new IllegalStateException("Matrix stack is already clear");
//
//        this.flush();
//        this.matrixStack.pop();
//        this.camera.combined.set(this.matrixStack.last());
//        this.globalTransform.set(this.globalMatrixStack.last());
//        this.flush();
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

        Border border = new Border(insets);
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

//        Border border = new Border(new Insets(lineWidth));
//        border.setRenderType(Border.RenderType.EFFECT);
//        border.setEffectSpeed(speed);
//        border.drawBorder(this, x, y, width, height);

        this.setLineThickness(lineWidth);
        this.withEffect(() -> {
            this.roundRect(x, y, width, height, radius);
        });
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

        Border border = new Border(insets);
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

    public float getWidth() {
        return Gdx.graphics.getWidth();
    }

    public float getHeight() {
        return Gdx.graphics.getHeight();
    }

    public void blit(int x, int y) {
        if (!this.rendering) return;

        this.batch.draw(this.texture, x, y);
    }

    public void blit(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.batch.draw(this.texture, x, y, width, height);
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
        return this.camera.combined;
    }

    @ApiStatus.Experimental
    public void setTransform(Matrix4 matrix) {
        if (!this.rendering) return;

        Matrix4 m = this.matrixStack.last();
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

        this.shapes.begin();
        this.state = State.SHAPES;
    }

    public enum State {
        BATCH, SHAPES
    }
}
