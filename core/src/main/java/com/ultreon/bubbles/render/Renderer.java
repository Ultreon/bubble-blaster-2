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
import com.ultreon.libs.text.v0.TextObject;
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
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final GL20 gl20;
    private final GL30 gl30;
    private final SpriteBatch batch;
    private final ShapeDrawer shapes;
    private final OrthographicCamera camera;
    private float lineWidth;
    private final MatrixStack matrixStack;
    private Texture curTexture;
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

    @ApiStatus.Internal
    public Renderer(ShapeDrawer shapes, SpriteBatch batch, OrthographicCamera camera) {
        this.font = this.game.getBitmapFont();
        this.gl20 = Gdx.gl20;
        this.gl30 = Gdx.gl30;
        this.batch = batch;
        this.shapes = shapes;
        this.camera = camera;
        this.matrixStack = new MatrixStack();
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
            throw new IllegalStateException("Matrix stack isn't cleared before renderer completes (should have 1 left): " + matrixStack.stack.size());

        this.matrixStack.stack.removeLast();

        this.disableDepthTest();
        this.disableBlend();

        this.batch.end();

        if (triggeredScissorLog) {
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

    public void outline(Rectangle rect) {
        if (!this.rendering) return;

        rectLine(rect.x, rect.y, rect.width, rect.height);
    }

    public void outline(Ellipse ellipse) {
        if (!this.rendering) return;

        ovalLine(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
    }

    public void outline(Circle ellipse) {
        if (!this.rendering) return;

        circleLine(ellipse.x, ellipse.y, ellipse.radius);
    }

    public void circle(float x, float y, float size) {
        if (!this.rendering) return;

        shapes.filledCircle(x, y, size / 2f);
    }

    public void circleLine(float x, float y, float size) {
        if (!this.rendering) return;

        shapes.circle(x, y, size / 2f, lineWidth);
    }

    public void fill(Shape2D s) {
        if (!this.rendering) return;

        if (s instanceof Circle circle) fill(circle);
        else if (s instanceof Ellipse ellipse) fill(ellipse);
        else if (s instanceof Rectangle rect) fill(rect);
        else if (s instanceof Polygon rect) fill(rect);
        else if (s instanceof Polyline rect) fill(rect);
    }

    public void fill(Circle ellipse) {
        if (!this.rendering) return;

        circle(ellipse.x, ellipse.y, ellipse.radius);
    }

    public void fill(Ellipse ellipse) {
        if (!this.rendering) return;

        ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
    }

    public void fill(Rectangle r) {
        if (!this.rendering) return;

        rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public void fill(Polygon polygon) {
        if (!this.rendering) return;

        polygon(polygon);
    }

    public void fill(Polyline polyline) {
        if (!this.rendering) return;

        polyline(polyline);
    }

    public void fill(Vec4i r) {
        if (!this.rendering) return;

        rect(r.x, r.y, r.z, r.w);
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
            case HORIZONTAL -> shapes.filledRectangle(x, y, width, height, color1.toGdx(), color2.toGdx(), color2.toGdx(), color1.toGdx());
            case VERTICAL -> shapes.filledRectangle(x, y, width, height, color2.toGdx(), color2.toGdx(), color1.toGdx(), color1.toGdx());
        }
    }

    public void line(int x1, int y1, int x2, int y2) {
        if (!this.rendering) return;

        shapes.line(x1, y1, x2, y2);
    }

    public void line(float x1, float y1, float x2, float y2) {
        if (!this.rendering) return;

        shapes.line(x1, y1, x2, y2);
    }

    public void rectLine(int x, int y, int width, int height) {
        if (!this.rendering) return;

        shapes.rectangle(x, y, width, height, lineWidth);
    }

    public void rectLine(float x, float y, float width, float height) {
        if (!this.rendering) return;

        shapes.rectangle(x, y, width, height, lineWidth);
    }

    public void rect(int x, int y, int width, int height) {
        if (!this.rendering) return;

        shapes.filledRectangle(x, y, width, height);
    }

    public void rect(float x, float y, float width, float height) {
        if (!this.rendering) return;

        shapes.filledRectangle(x, y, width, height);
    }

    public void roundRectLine(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (!this.rendering) return;

        shapes.rectangle(x, y, width, height, lineWidth, JoinType.SMOOTH);
    }

    public void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (!this.rendering) return;

        shapes.filledRectangle(x, y, width, height);
    }

    public void rect3DLine(int x, int y, int width, int height, boolean raised) {
        if (!this.rendering) return;

        shapes.rectangle(x, y, width, height, lineWidth);
    }

    public void rect3D(int x, int y, int width, int height, boolean raised) {
        if (!this.rendering) return;

        shapes.filledRectangle(x, y, width, height);
    }

    public void ovalLine(int x, int y, int width, int height) {
        if (!this.rendering) return;

        shapes.ellipse(x, y, width, height);
    }

    public void ellipse(int x, int y, int width, int height) {
        if (!this.rendering) return;

        shapes.filledEllipse(x, y, width, height);
    }

    public void ovalLine(float x, float y, float width, float height) {
        if (!this.rendering) return;

        shapes.ellipse(x, y, width, height);
    }

    public void ellipse(float x, float y, float width, float height) {
        if (!this.rendering) return;

        shapes.filledEllipse(x, y, width, height);
    }

    public void arcLine(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (!this.rendering) return;

        shapes.arc(x, y, width, startAngle, arcAngle);
    }

    public void arc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (!this.rendering) return;

        shapes.arc(x, y, width, startAngle, arcAngle);
    }

    public void polygonLine(Polygon p) {
        if (!this.rendering) return;

        this.shapes.polygon(p);
    }

    public void polygon(Polygon p) {
        if (!this.rendering) return;

        this.shapes.filledPolygon(p);
    }

    public void polyline(Polyline p) {
        if (!this.rendering) return;

        this.shapes.polygon(p.getVertices());
    }

    public void blit(Texture tex, float x, float y) {
        if (!this.rendering) return;

        batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, Color backgroundColor) {
        if (!this.rendering) return;

        setColor(backgroundColor);
        rect(x, y, tex.getWidth(), tex.getHeight());
        batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, float width, float height) {
        if (!this.rendering) return;

        batch.draw(tex, x, y + height, width, -height);
    }

    public void blit(Texture tex, float x, float y, float width, float height, Color backgroundColor) {
        if (!this.rendering) return;

        setColor(backgroundColor);
        rect(x, y, width, height);
        batch.draw(tex, x, y + height, width, -height);
    }

    public void drawText(BitmapFont font, String str, int x, int y) {
        if (!this.rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(String str, float x, float y) {
        if (!this.rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(TextObject str, int x, int y) {
        if (!this.rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(TextObject str, float x, float y) {
        if (!this.rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(BitmapFont font, String str, float x, float y) {
        if (!this.rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(BitmapFont font, TextObject str, int x, int y) {
        if (!this.rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(BitmapFont font, TextObject str, float x, float y) {
        if (!this.rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(String str, int x, int y, Anchor anchor) {
        if (!this.rendering) return;


        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, str);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(String str, float x, float y, Anchor anchor) {
        if (!this.rendering) return;


        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, str);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(TextObject str, int x, int y, Anchor anchor) {
        if (!this.rendering) return;


        String text = str.getText();

        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, text);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(TextObject str, float x, float y, Anchor anchor) {
        if (!this.rendering) return;


        String text = str.getText();

        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, text);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(BitmapFont font, String str, int x, int y, Anchor anchor) {
        if (!this.rendering) return;


        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, str);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(BitmapFont font, String str, float x, float y, Anchor anchor) {
        if (!this.rendering) return;


        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, str);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(BitmapFont font, TextObject str, int x, int y, Anchor anchor) {
        if (!this.rendering) return;


        String text = str.getText();

        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, text);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(BitmapFont font, TextObject str, float x, float y, Anchor anchor) {
        if (!this.rendering) return;


        String text = str.getText();

        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, text);

        float anchoredX = anchor.getX() * (layout.width / 2);
        float anchoredY = anchor.getY() * (layout.height / 2);
        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawMultiLineText(String str, int x, int y) {
        if (!this.rendering) return;

        for (String line : str.split("\n"))
            drawText(line, x, y += (int) font.getLineHeight());
    }

    public void drawMultiLineText(BitmapFont font, String str, int x, int y) {
        if (!this.rendering) return;

        for (String line : str.split("\n"))
            drawText(font, line, x, y += (int) font.getLineHeight());
    }

    public void drawWrappedText(String text, int x, int y, int maxWidth) {
        if (!this.rendering) return;

        List<String> lines = StringUtils.wrap(text, font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        drawMultiLineText(joined, x, y);
    }

    public void drawWrappedText(BitmapFont font, String text, int x, int y, int maxWidth) {
        if (!this.rendering) return;

        List<String> lines = StringUtils.wrap(text, font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        drawMultiLineText(font, joined, x, y);
    }

    public void drawWrappedText(TextObject text, int x, int y, int maxWidth) {
        if (!this.rendering) return;

        List<String> lines = StringUtils.wrap(text.getText(), font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        drawMultiLineText(joined, x, y);
    }

    public void drawWrappedText(BitmapFont font, TextObject text, int x, int y, int maxWidth) {
        if (!this.rendering) return;

        List<String> lines = StringUtils.wrap(text.getText(), font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        drawMultiLineText(font, joined, x, y);
    }

    public void drawTabbedText(String str, int x, int y) {
        if (!this.rendering) return;

        for (String line : str.split("\t"))
            drawText(line, x += (int) font.getLineHeight(), y);
    }

    public void drawTabbedText(BitmapFont font, String str, int x, int y) {
        if (!this.rendering) return;

        for (String line : str.split("\t"))
            drawText(font, line, x += (int) font.getLineHeight(), y);
    }

    public void drawCenteredText(String text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawCenteredString(this, text, new Vector2(x, y), this.font);
    }

    public void drawCenteredText(TextObject text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawCenteredString(this, text.getText(), new Vector2(x, y), this.font);
    }

    public void drawCenteredText(BitmapFont font, String text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawCenteredString(this, text, new Vector2(x, y), font);
    }

    public void drawLeftAnchoredText(String text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text, new Vector2(x, y), 0, this.font);
    }

    public void drawLeftAnchoredText(TextObject text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text.getText(), new Vector2(x, y), 0, this.font);
    }

    public void drawLeftAnchoredText(BitmapFont font, String text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text, new Vector2(x, y), 0, font);
    }

    public void drawRightAnchoredText(String text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text, new Vector2(x, y), 0, this.font);
    }

    public void drawRightAnchoredText(TextObject text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text.getText(), new Vector2(x, y), 0, this.font);
    }

    public void drawRightAnchoredText(BitmapFont font, String text, float x, float y) {
        if (!this.rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text, new Vector2(x, y), 0, font);
    }

    public void clear() {
        if (!this.rendering) return;

        ScreenUtils.clear(clearColor.toGdx(), true);
    }

    ////////////////////////////
    //     Transformation     //
    ////////////////////////////
    private void editMatrix(Function<Matrix4, Matrix4> editor) {
        this.batch.flush();
        Matrix4 matrix = editor.apply(this.camera.combined);
        this.matrixStack.last().set(this.camera.combined.set(matrix));
        this.batch.flush();
    }

    public void translate(float x, float y) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> {
            this.camera.translate(x, y);
            return this.camera.combined;
        });
    }

    public void translate(int x, int y) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> {
            this.camera.translate(x, y);
            return this.camera.combined;
        });
    }

    public void translate(float x, float y, float z) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> {
            this.camera.translate(x, y, z);
            return this.camera.combined;
        });
    }

    public void translate(int x, int y, int z) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> {
            this.camera.translate(x, y, z);
            return this.camera.combined;
        });
    }

    public void rotate(float theta) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> {
            this.camera.rotate(theta, 1, 0, 0);
            return this.camera.combined;
        });
    }

    public void rotate(float theta, float x, float y) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> {
            this.camera.rotateAround(new Vector3(x, y, 0), Vector3.X, theta);
            return this.camera.combined;
        });
    }

    public void scale(float width, float height, float depth) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> {
            this.camera.combined.scale(width, height, 0);
            return this.camera.combined;
        });
    }

    public void scale(float width, float height) {
        if (!this.rendering) return;

        this.editMatrix(matrix -> {
            this.camera.combined.scale(width, height, 0);
            return this.camera.combined;
        });
    }

    public void subInstance(Rectangle rectangle, Consumer<Renderer> consumer) {
        if (!this.rendering) return;

        this.subInstance((int) rectangle.x, (int) rectangle.y, (int) rectangle.width, (int) rectangle.height, consumer);
    }

    public void subInstance(int x, int y, int width, int height, Consumer<Renderer> consumer) {
        if (!this.rendering) return;

        this.pushMatrix();
        this.translate(x, y);
        this.pushScissor(x, y, width, height);
        consumer.accept(this);
        this.popScissor();
        this.popMatrix();
    }

    @CanIgnoreReturnValue
    public Rectangle pushScissor(Rectangle rect) {
        if (!this.rendering) return null;

        if (rect.width <= 0) throw new IllegalArgumentException(rect.width + " is an invalid scissor width.");
        if (rect.height <= 0) throw new IllegalArgumentException(rect.width + " is an invalid scissor height.");

        if (this.loggingScissors) {
            scissorDepth++;
            Debug.log("ScissorDebug", "Pushing scissor [%d]: %s".formatted(scissorDepth, rect));
        }

        rect.y = Gdx.graphics.getHeight() - rect.y - rect.height;

        this.batch.flush();
        ScissorStack.pushScissors(rect);
        this.batch.flush();
        return rect;
    }

    public void triggerScissorLog() {
        this.triggerScissorLog = true;
    }

    @CanIgnoreReturnValue
    public Rectangle pushScissor(float x, float y, float width, float height) {
        if (!this.rendering) return null;

        return pushScissor(new Rectangle(x, y, width, height));
    }

    @CanIgnoreReturnValue
    public Rectangle pushScissor(int x, int y, int width, int height) {
        if (!this.rendering) return null;

        return pushScissor(new Rectangle(x, y, width, height));
    }

    @CanIgnoreReturnValue
    public Rectangle popScissor() {
        if (!this.rendering) return null;

        if (this.loggingScissors) {
            Debug.log("ScissorDebug", "Popping scissor [%d]".formatted(scissorDepth));
            scissorDepth--;
        }

        this.batch.flush();
        Rectangle rectangle = ScissorStack.popScissors();
        this.batch.flush();
        return rectangle;
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
    }

    public void popMatrix() {
        if (!this.rendering) return;

        if (this.matrixStack.isClear()) throw new IllegalStateException("Matrix stack is already clear");

        this.batch.flush();
        this.matrixStack.pop();
        this.camera.combined.set(this.matrixStack.last());
        this.batch.flush();
    }

    public boolean hitClip(int x, int y, int width, int height) {
        return false;
    }

    public void drawEffectBox(int x, int y, int width, int height) {
        if (!this.rendering) return;

        drawEffectBox(x, y, width, height, new Insets(2, 2, 2, 2));
    }

    public void drawEffectBox(int x, int y, int width, int height, Insets insets) {
        if (!this.rendering) return;

        this.drawEffectBox(x, y, width, height, insets, BubbleBlasterConfig.DEFAULT_EFFECT_SPEEED.get());
    }

    public void drawEffectBox(int x, int y, int width, int height, Insets insets, int speed) {
        if (!this.rendering) return;

        Border border = new Border(insets);
        border.setRenderType(Border.RenderType.EFFECT);
        border.setEffectSpeed(speed);
        border.drawBorder(this, x, y, width, height);
    }

    @Deprecated(forRemoval = true)
    public void drawEffectBox(int x, int y, int width, int height, float strokeWidth) {
        if (!this.rendering) return;

        this.drawEffectBox(x, y, width, height, strokeWidth, BubbleBlasterConfig.DEFAULT_EFFECT_SPEEED.get());
    }

    @Deprecated(forRemoval = true)
    public void drawEffectBox(int x, int y, int width, int height, float borderWidth, int speed) {
        if (!this.rendering) return;

        Border border = new Border(new Insets((int) borderWidth));
        border.setRenderType(Border.RenderType.EFFECT);
        border.setEffectSpeed(speed);
        border.drawBorder(this, x, y, width, height);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height) {
        if (!this.rendering) return;

        drawRoundEffectBox(x, y, width, height, BubbleBlasterConfig.DEFAULT_EFFECT_SPEEED.get());
    }

    public void drawRoundEffectBox(int x, int y, int width, int height, int radius) {
        if (!this.rendering) return;

        drawRoundEffectBox(x, y, width, height, radius, 2);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height, int radius, int borderWidth) {
        if (!this.rendering) return;

        drawRoundEffectBox(x, y, width, height, radius, borderWidth, BubbleBlasterConfig.DEFAULT_EFFECT_SPEEED.get());
    }

    public void drawRoundEffectBox(int x, int y, int width, int height, int radius, int borderWidth, int speed) {
        if (!this.rendering) return;

        Border border = new Border(new Insets((int) borderWidth));
        border.setRenderType(Border.RenderType.EFFECT);
        border.setEffectSpeed(speed);
        border.drawBorder(this, x, y, width, height);
    }

    public void drawErrorEffectBox(int x, int y, int width, int height) {
        if (!this.rendering) return;

        drawErrorEffectBox(x, y, width, height, new Insets(2, 2, 2, 2));
    }

    public void drawErrorEffectBox(int x, int y, int width, int height, Insets insets) {
        if (!this.rendering) return;

        drawErrorEffectBox(x, y, width, height, insets, BubbleBlasterConfig.DEFAULT_EFFECT_SPEEED.get());
    }

    public void drawErrorEffectBox(int x, int y, int width, int height, Insets insets, int speed) {
        if (!this.rendering) return;

        Border border = new Border(insets);
        border.setRenderType(Border.RenderType.ERROR_EFFECT);
        border.setEffectSpeed(speed);
        border.drawBorder(this, x, y, width, height);
    }

    public void fillErrorEffect(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.fillScrollingGradient(x, y, width, height, BubbleBlasterConfig.DEFAULT_EFFECT_SPEEED.get(), ANIM_ERROR_COLOR_1, ANIM_ERROR_COLOR_2);
    }

    public void fillErrorEffect(int x, int y, int width, int height, int speed) {
        if (!this.rendering) return;

        this.fillScrollingGradient(x, y, width, height, speed, ANIM_ERROR_COLOR_1, ANIM_ERROR_COLOR_2);
    }

    public void fillEffect(int x, int y, int width, int height) {
        if (!this.rendering) return;

        this.fillScrollingGradient(x, y, width, height, BubbleBlasterConfig.DEFAULT_EFFECT_SPEEED.get(), ANIM_COLOR_1, ANIM_COLOR_2);
    }

    public void fillEffect(int x, int y, int width, int height, int speed) {
        if (!this.rendering) return;

        this.fillScrollingGradient(x, y, width, height, speed, ANIM_COLOR_1, ANIM_COLOR_2);
    }

    private void fillScrollingGradient(int x, int y, int width, int height, int speed, Color color1, Color color2) {
        float gameWidth = this.getWidth();
        float gameHeight = this.getHeight();
        var shiftX = (gameWidth * 2f * BubbleBlaster.getTicks() / (float) (BubbleBlaster.TPS * speed) - camera.combined.getTranslation(new Vector3()).x) % (gameWidth * 2);

        // Todo: do the effect!
        this.pushScissor(new Rectangle(x, y, width, height));
        this.fillGradient(-shiftX, 0, gameWidth, gameHeight, color1, color2, Axis2D.HORIZONTAL);
        this.fillGradient(-shiftX + gameWidth, 0, gameWidth, gameHeight, color2, color1, Axis2D.HORIZONTAL);
        this.fillGradient(-shiftX + gameWidth * 2, 0, gameWidth, gameHeight, color1, color2, Axis2D.HORIZONTAL);
        this.popScissor();

//        this.fillGradient(x, y, width, height, Color.rgb(color1), Color.rgb(color2), Axis2D.HORIZONTAL);
    }

    public float getWidth() {
        return Gdx.graphics.getWidth();
    }

    public float getHeight() {
        return Gdx.graphics.getHeight();
    }

//    @NotNull
//    private GradientPaint getEffectPaint(int speed) {
//        return getEffectPaint(speed, 0x00a0ff, 0x00ffa0);
//    }
//
//    @NotNull
//    private GradientPaint getErrorEffectPaint(int speed) {
//        return getEffectPaint(speed, 0xff3000, 0xffa000);
//    }
//
//    @NotNull
//    private GradientPaint getEffectPaint(int speed, int color1, int color2) {
//        var width = game.getScaledWidth();
//        var shiftX = (((double) width * 2) * BubbleBlaster.getTicks() / (double)(BubbleBlaster.TPS * speed)) - globalTranslation.x;
//        return new GradientPaint((float) shiftX - width, 0, Color.rgb(color1).toAwt(), (float) shiftX, 0f, Color.rgb(color2).toAwt(), true);
//    }

    public void blit(int x, int y) {
        if (!this.rendering) return;

        batch.draw(curTexture, x, y);
    }

    public void blit(int x, int y, int width, int height) {
        if (!this.rendering) return;

        batch.draw(curTexture, x, y, width, height);
    }

    public void blit(Identifier texture) {
        if (!this.rendering) return;

        this.curTexture = game.getTextureManager().getTexture(texture);
    }

    ////////////////////////
    //     Properties     //
    ////////////////////////
    @ApiStatus.Experimental
    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    @ApiStatus.Experimental
    public Matrix4 getTransform() {
        return matrixStack.last();
    }

    @ApiStatus.Experimental
    public void setTransform(Matrix4 matrix) {
        if (!this.rendering) return;

        Matrix4 m = matrixStack.last();
        if (m != null) {
            m.set(matrix);
            batch.setTransformMatrix(m);
        }
    }

    public void setLineWidth(float lineWidth) {
        if (!this.rendering) return;

        this.lineWidth = lineWidth;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setColor(Color c) {
        if (!this.rendering) return;
        if (c == null) return;

        font.setColor(c.toGdx());
        shapes.setColor(c.toGdx());
    }

    public void setColor(int r, int g, int b) {
        if (!this.rendering) return;

        setColor(Color.rgb(r, g, b));
    }

    public void setColor(float r, float g, float b) {
        if (!this.rendering) return;

        setColor(Color.rgb(r, g, b));
    }

    public void setColor(int r, int g, int b, int a) {
        if (!this.rendering) return;

        setColor(Color.rgba(r, g, b, a));
    }

    public void setColor(float r, float g, float b, float a) {
        if (!this.rendering) return;

        setColor(Color.rgba(r, g, b, a));
    }

    public void setColor(int argb) {
        if (!this.rendering) return;

        setColor(Color.argb(argb));
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

        setColor(Color.hex(hex));
    }

    public Color getColor() {
        var color = new com.badlogic.gdx.graphics.Color();
        com.badlogic.gdx.graphics.Color.abgr8888ToColor(color, shapes.getPackedColor());
        return Color.gdx(color);
    }

    public void setClearColor(Color color) {
        if (!this.rendering) return;

        gl20.glClearColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public void setClearColor(int red, int green, int blue) {
        if (!this.rendering) return;

        setClearColor(Color.rgb(red, green, blue));
    }

    public void setClearColor(float red, float green, float blue) {
        if (!this.rendering) return;

        setClearColor(Color.rgb(red, green, blue));
    }

    public void setClearColor(int red, int green, int blue, int alpha) {
        if (!this.rendering) return;

        setClearColor(Color.rgba(red, green, blue, alpha));
    }

    public void setClearColor(float red, float green, float blue, float alpha) {
        if (!this.rendering) return;

        setClearColor(Color.rgba(red, green, blue, alpha));
    }

    public void setClearColor(int argb) {
        if (!this.rendering) return;

        setClearColor(Color.argb(argb));
    }

    public void setClearColor(String hex) {
        if (!this.rendering) return;

        setClearColor(Color.hex(hex));
    }

    public Color getClearColor() {
        return clearColor;
    }

    public void setFont(BitmapFont font) {
        if (!this.rendering) return;

        this.font = font;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void roundedLine(float x1, float y1, float x2, float y2) {
        shapes.path(Array.with(new Vector2(x1, y1), new Vector2(x2, y2)), lineWidth, JoinType.SMOOTH, false);
    }

    public void setPixel(Pixel pixel) {
        shapes.filledRectangle(pixel.getX(), pixel.getY(), 1, 1, Color.rgba(pixel.getColor().getRed(), pixel.getColor().getGreen(), pixel.getColor().getBlue(), pixel.getColor().getAlpha()).toGdx());
    }

    @Override
    public String toString() {
        return "Renderer{" +
                "rendering=" + rendering +
                '}';
    }

    public enum State {
        BATCH, SHAPES
    }
}
