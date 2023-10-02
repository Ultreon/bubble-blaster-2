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
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
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
import com.ultreon.libs.commons.v0.vector.Vec4i;
import com.ultreon.libs.text.v1.TextObject;
import org.jetbrains.annotations.ApiStatus;
import space.earlygrey.shapedrawer.DefaultSideEstimator;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

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
    private final ShapeDrawer shapes;
    private final OrthographicCamera camera;
    private float lineWidth;
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

    @ApiStatus.Internal
    public Renderer(ShapeDrawer shapes, SpriteBatch batch, OrthographicCamera camera) {
        this.font = this.game.getBitmapFont();
        this.gl20 = Gdx.gl20;
        this.gl30 = Gdx.gl30;
        this.batch = batch;
        this.shapes = shapes;
        this.camera = camera;
        this.matrixStack = new MatrixStack();
        this.globalMatrixStack = new MatrixStack();
        this.matrixStack.stack.removeLast();
        this.shapes.setSideEstimator(new DefaultSideEstimator(50, 360, 2f));

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

        this.rendering = true;

        this.clear();
        this.enableBlend();
        this.enableDepthTest();
    }

    @ApiStatus.Internal
    public void end() {
        if (!this.rendering)
            throw new IllegalStateException("Renderer isn't rendering yet");

        this.camera.combined.set(this.backupMatrix);

        if (!this.matrixStack.isClear())
            this.clearMatrixStack();

        this.matrixStack.stack.removeLast();

        this.disableDepthTest();
        this.disableBlend();

        this.batch.end();

        if (this.triggeredScissorLog) {
            this.triggeredScissorLog = false;
            this.loggingScissors = false;
        }

        this.rendering = false;
    }

    public void enableBlend() {
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void enableDepthTest() {
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
    }

    public void disableBlend() {
        Gdx.gl20.glDisable(GL20.GL_BLEND);
    }

    public void disableDepthTest() {
        Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);
    }

    @Deprecated(forRemoval = true)
    public void outline(Rectangle rect) {
        if (!this.rendering) return;

        this.box(rect.x, rect.y, rect.width, rect.height);
    }

    @Deprecated(forRemoval = true)
    public void outline(Ellipse ellipse) {
        if (!this.rendering) return;

        this.ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
    }

    @Deprecated(forRemoval = true)
    public void outline(Circle ellipse) {
        if (!this.rendering) return;

        this.circle(ellipse.x, ellipse.y, ellipse.radius);
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

    @Deprecated(forRemoval = true)
    public void fillCircle(float x, float y, float size) {
        if (!this.rendering) return;

        this.shapes.filledCircle(x, y, size / 2f);
    }

    public void fillCircle(float x, float y, float size, Color color) {
        if (!this.rendering) return;

        this.shapes.filledCircle(x, y, size / 2f, color.toGdx());
    }

    @Deprecated(forRemoval = true)
    public void circle(float x, float y, float size) {
        if (!this.rendering) return;

        this.shapes.circle(x, y, size / 2f, this.lineWidth);
    }

    public void circle(float x, float y, float size, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        this.shapes.circle(x, y, size / 2f, this.lineWidth);
    }

    @Deprecated(forRemoval = true)
    public void fill(Shape2D s) {
        if (!this.rendering) return;

        if (s instanceof Circle circle) this.fill(circle);
        else if (s instanceof Ellipse ellipse) this.fill(ellipse);
        else if (s instanceof Rectangle rect) this.fill(rect);
        else if (s instanceof Polygon rect) this.fill(rect);
        else if (s instanceof Polyline rect) this.fill(rect);
    }

    @Deprecated(forRemoval = true)
    public void fill(Circle ellipse) {
        if (!this.rendering) return;

        this.fillCircle(ellipse.x, ellipse.y, ellipse.radius);
    }

    @Deprecated(forRemoval = true)
    public void fill(Ellipse ellipse) {
        if (!this.rendering) return;

        this.fillEllipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
    }

    @Deprecated(forRemoval = true)
    public void fill(Rectangle r) {
        if (!this.rendering) return;

        this.fill(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @Deprecated(forRemoval = true)
    public void fill(Polygon polygon) {
        if (!this.rendering) return;

        this.fillPolygon(polygon);
    }

    @Deprecated(forRemoval = true)
    public void fill(Polyline polyline) {
        if (!this.rendering) return;

        this.polyline(polyline);
    }

    @Deprecated(forRemoval = true)
    public void fill(Vec4i r) {
        if (!this.rendering) return;

        this.fill(r.x, r.y, r.z, r.w);
    }

    public void fill(Shape2D s, Color color) {
        if (!this.rendering) return;

        if (s instanceof Circle circle) this.fill(circle, color);
        else if (s instanceof Ellipse ellipse) this.fill(ellipse, color);
        else if (s instanceof Rectangle rect) this.fill(rect, color);
        else if (s instanceof Polygon rect) this.fill(rect, color);
        else if (s instanceof Polyline rect) this.fill(rect, color);
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

    public void fill(Vec4i r, Color color) {
        if (!this.rendering) return;

        this.fill(r.x, r.y, r.z, r.w);
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

        switch (axis) {
            case HORIZONTAL -> this.shapes.filledRectangle(x, y, width, height, color1.toGdx(), color2.toGdx(), color2.toGdx(), color1.toGdx());
            case VERTICAL -> this.shapes.filledRectangle(x, y, width, height, color2.toGdx(), color2.toGdx(), color1.toGdx(), color1.toGdx());
        }
    }

    @Deprecated(forRemoval = true)
    public void line(int x1, int y1, int x2, int y2) {
        if (!this.rendering) return;

        this.shapes.line(x1, y1, x2, y2);
    }

    @Deprecated(forRemoval = true)
    public void line(float x1, float y1, float x2, float y2) {
        if (!this.rendering) return;

        this.shapes.line(x1, y1, x2, y2);
    }

    public void line(float x1, float y1, float x2, float y2, Color color) {
        if (!this.rendering) return;

        this.shapes.line(x1, y1, x2, y2, color.toGdx());
    }

    @Deprecated(forRemoval = true)
    public void box(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.shapes.rectangle(x, y, width, height, this.lineWidth);
    }

    @Deprecated(forRemoval = true)
    public void box(float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.shapes.rectangle(x, y, width, height, this.lineWidth);
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

    @Deprecated(forRemoval = true)
    public void fill(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.shapes.filledRectangle(x, y, width, height);
    }

    @Deprecated(forRemoval = true)
    public void fill(float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.shapes.filledRectangle(x, y, width, height);
    }

    public void fill(float x, int y, float width, int height, Color color) {
        if (!this.rendering) return;

        this.shapes.filledRectangle(x, y, width, height, color.toGdx());
    }

    public void fill(float x, float y, float width, float height, Color color) {
        if (!this.rendering) return;

        this.shapes.filledRectangle(x, y, width, height, color.toGdx());
    }

    public void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (!this.rendering) return;

        this.shapes.rectangle(x, y, width, height, this.lineWidth, JoinType.SMOOTH);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (!this.rendering) return;

        this.shapes.filledRectangle(x, y, width, height);
    }

    @Deprecated(forRemoval = true)
    public void rect3D(int x, int y, int width, int height, boolean raised) {
        if (!this.rendering) return;

        this.shapes.rectangle(x, y, width, height, this.lineWidth);
    }

    @Deprecated(forRemoval = true)
    public void fillRect3D(int x, int y, int width, int height, boolean raised) {
        if (!this.rendering) return;

        this.shapes.filledRectangle(x, y, width, height);
    }

    public void ellipse(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.shapes.ellipse(x, y, width, height);
    }

    public void ellipse(float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.shapes.ellipse(x, y, width, height);
    }

    public void ellipse(int x, int y, int width, int height, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        this.shapes.ellipse(x, y, width, height);
    }

    public void ellipse(float x, float y, float width, float height, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        this.shapes.ellipse(x, y, width, height);
    }

    public void fillEllipse(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.shapes.filledEllipse(x, y, width, height);
    }

    public void fillEllipse(float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.shapes.filledEllipse(x, y, width, height);
    }

    public void fillEllipse(int x, int y, int width, int height, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        this.shapes.filledEllipse(x, y, width, height);
    }

    public void fillEllipse(float x, float y, float width, float height, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        this.shapes.filledEllipse(x, y, width, height);
    }

    @Deprecated(forRemoval = true)
    public void arcLine(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (!this.rendering) return;

        this.shapes.arc(x, y, width, startAngle, arcAngle);
    }

    public void arc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (!this.rendering) return;

        this.shapes.arc(x, y, width, startAngle, arcAngle);
    }

    @Deprecated(forRemoval = true)
    public void polygon(Polygon p) {
        if (!this.rendering) return;

        this.shapes.polygon(p);
    }

    public void polygon(Polygon p, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        this.shapes.polygon(p);
    }

    @Deprecated(forRemoval = true)
    public void fillPolygon(Polygon p) {
        if (!this.rendering) return;

        this.shapes.filledPolygon(p);
    }

    public void fillPolygon(Polygon p, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        this.shapes.filledPolygon(p);
    }

    @Deprecated(forRemoval = true)
    public void polyline(Polyline p) {
        if (!this.rendering) return;

        this.shapes.polygon(p.getVertices());
    }

    public void polyline(Polyline p, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        this.shapes.polygon(p.getVertices());
    }

    public void blit(Texture tex, float x, float y) {
        if (!this.rendering) return;

        this.batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, Color backgroundColor) {
        if (!this.rendering) return;

        this.fill(x, y, tex.getWidth(), tex.getHeight(), backgroundColor);
        this.batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, float width, float height) {
        if (!this.rendering) return;

        this.batch.draw(tex, x, y + height, width, -height);
    }

    public void blit(Texture tex, float x, float y, float width, float height, Color backgroundColor) {
        if (!this.rendering) return;

        this.fill(x, y, width, height, backgroundColor);
        this.batch.draw(tex, x, y + height, width, -height);
    }

    @Deprecated(forRemoval = true)
    public void drawText(BitmapFont font, String str, int x, int y) {
        if (!this.rendering) return;

        font.setColor(this.getColor().toGdx());
        font.draw(this.batch, str, x, y);
    }

    public void drawText(BitmapFont font, String str, int x, int y, Color color) {
        if (!this.rendering) return;

        font.setColor(color.toGdx());
        font.draw(this.batch, str, x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(String str, float x, float y) {
        if (!this.rendering) return;

        this.font.setColor(this.getColor().toGdx());
        this.font.draw(this.batch, str, x, y);
    }

    @ApiStatus.Experimental
    public void drawText(TextObject str, int x, int y) {
        if (!this.rendering) return;

        this.font.setColor(this.getColor().toGdx());
        this.font.draw(this.batch, str.getText(), x, y);
    }

    @ApiStatus.Experimental
    public void drawText(TextObject str, float x, float y) {
        if (!this.rendering) return;

        this.font.setColor(this.getColor().toGdx());
        this.font.draw(this.batch, str.getText(), x, y);
    }

    @ApiStatus.Experimental
    public void drawText(TextObject str, int x, int y, Color color) {
        if (!this.rendering) return;

        this.font.setColor(color.toGdx());
        this.font.draw(this.batch, str.getText(), x, y);
    }

    @ApiStatus.Experimental
    public void drawText(TextObject str, float x, float y, Color color) {
        if (!this.rendering) return;

        this.font.setColor(color.toGdx());
        this.font.draw(this.batch, str.getText(), x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(BitmapFont font, String str, float x, float y) {
        if (!this.rendering) return;

        font.setColor(this.getColor().toGdx());
        font.draw(this.batch, str, x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(BitmapFont font, TextObject str, int x, int y) {
        if (!this.rendering) return;

        font.setColor(this.getColor().toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(BitmapFont font, TextObject str, float x, float y) {
        if (!this.rendering) return;

        font.setColor(this.getColor().toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    public void drawText(BitmapFont font, String str, float x, float y, Color color) {
        if (!this.rendering) return;

        font.setColor(color.toGdx());
        font.draw(this.batch, str, x, y);
    }

    @ApiStatus.Experimental
    public void drawText(BitmapFont font, TextObject str, int x, int y, Color color) {
        if (!this.rendering) return;

        font.setColor(color.toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    @ApiStatus.Experimental
    public void drawText(BitmapFont font, TextObject str, float x, float y, Color color) {
        if (!this.rendering) return;

        font.setColor(color.toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(String str, int x, int y, Anchor anchor) {
        if (!this.rendering) return;


        GlyphLayout layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(this.font, str);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        this.font.setColor(this.getColor().toGdx());
        this.font.draw(this.batch, str, x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(String str, float x, float y, Anchor anchor) {
        if (!this.rendering) return;


        GlyphLayout layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(this.font, str);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        this.font.setColor(this.getColor().toGdx());
        this.font.draw(this.batch, str, x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(TextObject str, int x, int y, Anchor anchor) {
        if (!this.rendering) return;


        String text = str.getText();

        GlyphLayout layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(this.font, text);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        this.font.setColor(this.getColor().toGdx());
        this.font.draw(this.batch, str.getText(), x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(TextObject str, float x, float y, Anchor anchor) {
        if (!this.rendering) return;


        String text = str.getText();

        GlyphLayout layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(this.font, text);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        this.font.setColor(this.getColor().toGdx());
        this.font.draw(this.batch, str.getText(), x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(BitmapFont font, String str, int x, int y, Anchor anchor) {
        if (!this.rendering) return;


        GlyphLayout layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(font, str);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(this.getColor().toGdx());
        font.draw(this.batch, str, x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(BitmapFont font, String str, float x, float y, Anchor anchor) {
        if (!this.rendering) return;


        GlyphLayout layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(font, str);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(this.getColor().toGdx());
        font.draw(this.batch, str, x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(BitmapFont font, TextObject str, int x, int y, Anchor anchor) {
        if (!this.rendering) return;


        String text = str.getText();

        GlyphLayout layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(font, text);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(this.getColor().toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    @Deprecated(forRemoval = true)
    public void drawText(BitmapFont font, TextObject str, float x, float y, Anchor anchor) {
        if (!this.rendering) return;


        String text = str.getText();

        GlyphLayout layout = this.glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            this.glyphLayout.set(layout);
        }

        layout.setText(font, text);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(this.getColor().toGdx());
        font.draw(this.batch, str.getText(), x, y);
    }

    public void drawMultiLineText(String str, int x, int y) {
        if (!this.rendering) return;

        for (String line : str.split("\n"))
            this.drawText(line, x, y += (int) this.font.getLineHeight());
    }

    public void drawMultiLineText(BitmapFont font, String str, int x, int y) {
        if (!this.rendering) return;

        for (String line : str.split("\n"))
            this.drawText(font, line, x, y += (int) font.getLineHeight());
    }

    @Deprecated
    public void drawWrappedText(String text, int x, int y, int maxWidth) {
        if (!this.rendering) return;

        List<String> lines = StringUtils.wrap(text, this.font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(joined, x, y);
    }

    @Deprecated
    public void drawWrappedText(BitmapFont font, String text, int x, int y, int maxWidth) {
        if (!this.rendering) return;

        List<String> lines = StringUtils.wrap(text, font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(font, joined, x, y);
    }

    @Deprecated
    public void drawWrappedText(TextObject text, int x, int y, int maxWidth) {
        if (!this.rendering) return;

        List<String> lines = StringUtils.wrap(text.getText(), this.font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(joined, x, y);
    }

    @Deprecated
    public void drawWrappedText(BitmapFont font, TextObject text, int x, int y, int maxWidth) {
        if (!this.rendering) return;

        List<String> lines = StringUtils.wrap(text.getText(), font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(font, joined, x, y);
    }

    public void drawWrappedText(String text, int x, int y, int maxWidth, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        List<String> lines = StringUtils.wrap(text, this.font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(joined, x, y);
    }

    public void drawWrappedText(BitmapFont font, String text, int x, int y, int maxWidth, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        List<String> lines = StringUtils.wrap(text, font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(font, joined, x, y);
    }

    public void drawWrappedText(TextObject text, int x, int y, int maxWidth, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        List<String> lines = StringUtils.wrap(text.getText(), this.font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(joined, x, y);
    }

    public void drawWrappedText(BitmapFont font, TextObject text, int x, int y, int maxWidth, Color color) {
        if (!this.rendering) return;

        this.setColor(color);
        List<String> lines = StringUtils.wrap(text.getText(), font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        this.drawMultiLineText(font, joined, x, y);
    }

    public void drawTabbedText(String str, int x, int y) {
        if (!this.rendering) return;

        for (String line : str.split("\t"))
            this.drawText(line, x += (int) this.font.getLineHeight(), y);
    }

    public void drawTabbedText(BitmapFont font, String str, int x, int y) {
        if (!this.rendering) return;

        for (String line : str.split("\t"))
            this.drawText(font, line, x += (int) font.getLineHeight(), y);
    }

    @Deprecated(forRemoval = true)
    public void drawTextCenter(String text, float x, float y) {
        this.drawTextCenter(text, x, y, this.getColor());
    }

    @Deprecated(forRemoval = true)
    public void drawTextCenter(String text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawCenteredString(this, text, new Vector2(x, y), this.font, color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextCenter(TextObject text, float x, float y) {
        this.drawTextCenter(text, x, y, this.getColor());
    }

    @Deprecated(forRemoval = true)
    public void drawTextCenter(TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawCenteredString(this, text.getText(), new Vector2(x, y), this.font, color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextCenter(BitmapFont font, String text, float x, float y) {
        this.drawTextCenter(font, text, x, y, this.getColor());
    }

    public void drawTextCenter(BitmapFont font, String text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawCenteredString(this, text, new Vector2(x, y), font, color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextCenter(BitmapFont font, TextObject text, float x, float y) {
        this.drawTextCenter(font, text, x, y, this.getColor());
    }

    public void drawTextCenter(BitmapFont font, TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawCenteredString(this, text, new Vector2(x, y), font, color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextLeft(String text, float x, float y) {
        this.drawTextLeft(text, x, y, this.getColor());
    }

    @Deprecated(forRemoval = true)
    public void drawTextLeft(String text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text, new Vector2(x, y), 0, this.font, color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextLeft(TextObject text, float x, float y) {
        this.drawTextLeft(text, x, y, this.getColor());
    }

    @Deprecated(forRemoval = true)
    public void drawTextLeft(TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text.getText(), new Vector2(x, y), 0, this.font, color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextLeft(BitmapFont font, TextObject text, float x, float y) {
        this.drawTextLeft(font, text, x, y, this.getColor());
    }

    public void drawTextLeft(BitmapFont font, TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text.getText(), new Vector2(x, y), 0, font, color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextLeft(BitmapFont font, String text, float x, float y) {
        this.drawTextLeft(font, text, x, y, this.getColor());
    }

    public void drawTextLeft(BitmapFont font, String text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text, new Vector2(x, y), 0, font, color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextRight(String text, float x, float y) {
        this.drawTextRight(text, x, y, this.getColor());
    }

    @Deprecated(forRemoval = true)
    public void drawTextRight(String text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text, new Vector2(x, y), 0, this.font, color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextRight(TextObject text, float x, float y) {
        this.drawTextRight(text, x, y, this.color);
    }

    @Deprecated(forRemoval = true)
    public void drawTextRight(TextObject text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text.getText(), new Vector2(x, y), 0, this.font, color);
    }

    public void drawTextRight(BitmapFont font, String text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text, new Vector2(x, y), 0, font);
    }

    public void drawTextRight(BitmapFont font, String text, float x, float y, Color color) {
        if (!this.rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text, new Vector2(x, y), 0, font, color);
    }

    public void clear() {
        if (!this.rendering) return;

        ScreenUtils.clear(this.clearColor.toGdx(), true);
    }

    ////////////////////////////
    //     Transformation     //
    ////////////////////////////
    private void editMatrix(Function<Matrix4, Matrix4> editor) {
        this.batch.flush();
        Matrix4 matrix = editor.apply(this.camera.combined);
        editor.apply(this.globalTransform);
        this.matrixStack.last().set(this.camera.combined.set(matrix));
        this.batch.flush();
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
    public void subInstance(Rectangle rectangle, Consumer<Renderer> consumer) {
        if (!this.rendering) return;

        this.subInstance((int) rectangle.x, (int) rectangle.y, (int) rectangle.width, (int) rectangle.height, consumer);
    }

    @Deprecated
    public void subInstance(int x, int y, int width, int height, Consumer<Renderer> consumer) {
        if (!this.rendering) return;

        this.pushMatrix();
        this.translate(x, y);
        this.scissored(x, y, width, height, () -> {
            if (BubbleBlasterConfig.DEBUG_LOG_EMPTY_SCISSORS.getOrDefault()) {
                String formatted = "Empty Scissor @ %d, %d".formatted(x, y);
                BubbleBlaster.LOGGER.warn(formatted);
                Debug.notifyOnce(UUID.fromString("b3d5760c-fbf6-4d61-b64c-0c0ef9d7f1a3"), "Scissor Debug", formatted);
            }
        });
        consumer.accept(this);
        this.popMatrix();
    }

    @CheckReturnValue
    @ApiStatus.Internal
    private boolean pushScissor(Rectangle rect) {
        if (!this.rendering) return false;

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
            this.batch.flush();
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

        if (!BubbleBlasterConfig.DEBUG_DISABLE_SCISSORS.getOrDefault()) {
            this.batch.flush();
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

        this.batch.flush();
        while (ScissorStack.peekScissors() != null) {
            ScissorStack.popScissors();
        }
        this.batch.flush();
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
        if (!this.rendering) return;

        if (this.matrixStack.isClear()) throw new IllegalStateException("Matrix stack is already clear");

        this.batch.flush();
        this.matrixStack.pop();
        this.camera.combined.set(this.matrixStack.last());
        this.globalTransform.set(this.globalMatrixStack.last());
        this.batch.flush();
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

    @Deprecated(forRemoval = true)
    public void drawEffectBox(int x, int y, int width, int height, float strokeWidth) {
        if (!this.rendering) return;

        this.drawEffectBox(x, y, width, height, strokeWidth, BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.getOrDefault());
    }

    @Deprecated(forRemoval = true)
    public void drawEffectBox(int x, int y, int width, int height, float borderWidth, float speed) {
        if (!this.rendering) return;

        Border border = new Border(new Insets((int) borderWidth));
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

        this.drawRoundEffectBox(x, y, width, height, radius, 2);
    }

    public void drawRoundEffectBox(float x, float y, float width, float height, float radius, int borderWidth) {
        if (!this.rendering) return;

        this.drawRoundEffectBox(x, y, width, height, radius, borderWidth, BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.getOrDefault());
    }

    public void drawRoundEffectBox(float x, float y, float width, float height, float radius, int borderWidth, float speed) {
        if (!this.rendering) return;

        Border border = new Border(new Insets(borderWidth));
        border.setRenderType(Border.RenderType.EFFECT);
        border.setEffectSpeed(speed);
        border.drawBorder(this, x, y, width, height);
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

    public void setLineWidth(float lineWidth) {
        if (!this.rendering) return;

        this.lineWidth = lineWidth;
    }

    public float getLineWidth() {
        return this.lineWidth;
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

    public Color getColor() {
        var color = new com.badlogic.gdx.graphics.Color();
        com.badlogic.gdx.graphics.Color.abgr8888ToColor(color, this.shapes.getPackedColor());
        return Color.gdx(color);
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
        this.shapes.path(Array.with(new Vector2(x1, y1), new Vector2(x2, y2)), this.lineWidth, JoinType.SMOOTH, false);
    }

    public void setPixel(Pixel pixel) {
        this.shapes.filledRectangle(pixel.getX(), pixel.getY(), 1, 1, Color.rgba(pixel.getColor().getRed(), pixel.getColor().getGreen(), pixel.getColor().getBlue(), pixel.getColor().getAlpha()).toGdx());
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

    public void outline(Shape2D shape, Color color) {
        if (shape instanceof Circle circle) this.outline(circle, color);
        else if (shape instanceof Rectangle rectangle) this.outline(rectangle, color);
        else if (shape instanceof Polygon polygon) this.outline(polygon, color);
        else if (shape instanceof Ellipse ellipse) this.outline(ellipse, color);
        else throw new UnsupportedOperationException("Shape not supported: " + shape.getClass().getName());
    }

    public enum State {
        BATCH, SHAPES
    }
}
