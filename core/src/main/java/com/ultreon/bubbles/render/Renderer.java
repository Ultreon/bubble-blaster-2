/////////////////////
//     Package     //
/////////////////////
package com.ultreon.bubbles.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.ultreon.bubbles.Axis2D;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.commons.util.StringUtils;
import com.ultreon.libs.commons.v0.Anchor;
import com.ultreon.libs.commons.v0.Identifier;
import com.badlogic.gdx.math.Vector2;
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
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
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
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final GL20 gl20;
    private final GL30 gl30;
    private final Batch batch;
    private final ShapeDrawer shapes;
    private final OrthographicCamera camera;
    private float lineWidth;
    private final Deque<Matrix4> matrixStack = new ArrayDeque<>();
    private Texture curTexture;
    private BitmapFont font;
    private final ThreadLocal<GlyphLayout> glyphLayout = new ThreadLocal<>();
    private final Color clearColor = Color.BLACK;
    private Color color;
    private boolean rendering;

    @ApiStatus.Internal
    public Renderer(ShapeDrawer shapes, OrthographicCamera camera) {
        this.font = this.game.getBitmapFont();
        this.gl20 = Gdx.gl20;
        this.gl30 = Gdx.gl30;
        this.batch = shapes.getBatch();
        this.shapes = shapes;
        this.camera = camera;
        this.shapes.setSideEstimator(new DefaultSideEstimator(50, 360, 2f));
    }

    @ApiStatus.Internal
    public void begin() {
        if (this.rendering)
            throw new IllegalStateException("Renderer is already rendering");

        this.batch.setProjectionMatrix(this.camera.combined);
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

//        if (!ScissorStackMixin.getScissors().isEmpty())
//            throw new IllegalStateException("Scissor stack isn't cleared before renderer completes");

        if (!matrixStack.isEmpty())
            throw new IllegalStateException("Matrix stack isn't cleared before renderer completes");

        this.disableDepthTest();
        this.disableBlend();

        this.batch.end();

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
        if (!rendering) return;

        rectLine(rect.x, rect.y, rect.width, rect.height);
    }

    public void outline(Ellipse ellipse) {
        if (!rendering) return;

        ovalLine(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
    }

    public void outline(Circle ellipse) {
        if (!rendering) return;

        circleLine(ellipse.x, ellipse.y, ellipse.radius);
    }

    public void circle(float x, float y, float size) {
        if (!rendering) return;

        shapes.filledCircle(x, y, size / 2f);
    }

    public void circleLine(float x, float y, float size) {
        if (!rendering) return;

        shapes.circle(x, y, size / 2f, lineWidth);
    }

    public void fill(Shape2D s) {
        if (!rendering) return;

        if (s instanceof Circle circle) fill(circle);
        else if (s instanceof Ellipse ellipse) fill(ellipse);
        else if (s instanceof Rectangle rect) fill(rect);
        else if (s instanceof Polygon rect) fill(rect);
        else if (s instanceof Polyline rect) fill(rect);
    }

    public void fill(Circle ellipse) {
        if (!rendering) return;

        circle(ellipse.x, ellipse.y, ellipse.radius);
    }

    public void fill(Ellipse ellipse) {
        if (!rendering) return;

        ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
    }

    public void fill(Rectangle r) {
        if (!rendering) return;

        rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public void fill(Polygon polygon) {
        if (!rendering) return;

        polygon(polygon);
    }

    public void fill(Polyline polyline) {
        if (!rendering) return;

        polyline(polyline);
    }

    public void fill(Vec4i r) {
        if (!rendering) return;

        rect(r.x, r.y, r.z, r.w);
    }

    public void fillGradient(Rectangle bounds, Color color1, Color color2) {
        if (!rendering) return;

        this.fillGradient(bounds.x, bounds.y, bounds.width, bounds.height, color1, color2, Axis2D.VERTICAL);
    }

    public void fillGradient(Rectangle bounds, Color color1, Color color2, Axis2D axis) {
        if (!rendering) return;

        this.fillGradient(bounds.x, bounds.y, bounds.width, bounds.height, color1, color2, axis);
    }

    public void fillGradient(float x, float y, float width, float height, Color color1, Color color2) {
        if (!rendering) return;

        this.fillGradient(x, y, width, height, color1, color2, Axis2D.VERTICAL);
    }

    public void fillGradient(float x, float y, float width, float height, Color color1, Color color2, Axis2D axis) {
        if (!rendering) return;

        switch (axis) {
            case HORIZONTAL -> shapes.filledRectangle(x, y, width, height, color1.toGdx(), color2.toGdx(), color2.toGdx(), color1.toGdx());
            case VERTICAL -> shapes.filledRectangle(x, y, width, height, color2.toGdx(), color2.toGdx(), color1.toGdx(), color1.toGdx());
        }
    }

    public void line(int x1, int y1, int x2, int y2) {
        if (!rendering) return;

        shapes.line(x1, y1, x2, y2);
    }

    public void line(float x1, float y1, float x2, float y2) {
        if (!rendering) return;

        shapes.line(x1, y1, x2, y2);
    }

    public void rectLine(int x, int y, int width, int height) {
        if (!rendering) return;

        shapes.rectangle(x, y, width, height, lineWidth);
    }

    public void rectLine(float x, float y, float width, float height) {
        if (!rendering) return;

        shapes.rectangle(x, y, width, height, lineWidth);
    }

    public void rect(int x, int y, int width, int height) {
        if (!rendering) return;

        shapes.filledRectangle(x, y, width, height);
    }

    public void rect(float x, float y, float width, float height) {
        if (!rendering) return;

        shapes.filledRectangle(x, y, width, height);
    }

    public void roundRectLine(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (!rendering) return;

        shapes.rectangle(x, y, width, height, lineWidth, JoinType.SMOOTH);
    }

    public void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (!rendering) return;

        shapes.filledRectangle(x, y, width, height);
    }

    public void rect3DLine(int x, int y, int width, int height, boolean raised) {
        if (!rendering) return;

        shapes.rectangle(x, y, width, height, lineWidth);
    }

    public void rect3D(int x, int y, int width, int height, boolean raised) {
        if (!rendering) return;

        shapes.filledRectangle(x, y, width, height);
    }

    public void ovalLine(int x, int y, int width, int height) {
        if (!rendering) return;

        shapes.ellipse(x, y, width, height);
    }

    public void ellipse(int x, int y, int width, int height) {
        if (!rendering) return;

        shapes.filledEllipse(x, y, width, height);
    }

    public void ovalLine(float x, float y, float width, float height) {
        if (!rendering) return;

        shapes.ellipse(x, y, width, height);
    }

    public void ellipse(float x, float y, float width, float height) {
        if (!rendering) return;

        shapes.filledEllipse(x, y, width, height);
    }

    public void arcLine(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (!rendering) return;

        shapes.arc(x, y, width, startAngle, arcAngle);
    }

    public void arc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (!rendering) return;

        shapes.arc(x, y, width, startAngle, arcAngle);
    }

    public void polygonLine(Polygon p) {
        if (!rendering) return;

        this.shapes.polygon(p);
    }

    public void polygon(Polygon p) {
        if (!rendering) return;

        this.shapes.filledPolygon(p);
    }

    public void polyline(Polyline p) {
        if (!rendering) return;

        this.shapes.polygon(p.getVertices());
    }

    public void blit(Texture tex, float x, float y) {
        if (!rendering) return;

        batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, Color backgroundColor) {
        if (!rendering) return;

        setColor(backgroundColor);
        rect(x, y, tex.getWidth(), tex.getHeight());
        batch.draw(tex, x, y + tex.getHeight(), tex.getWidth(), -tex.getHeight());
    }

    public void blit(Texture tex, float x, float y, float width, float height) {
        if (!rendering) return;

        batch.draw(tex, x, y + height, width, -height);
    }

    public void blit(Texture tex, float x, float y, float width, float height, Color backgroundColor) {
        if (!rendering) return;

        setColor(backgroundColor);
        rect(x, y, width, height);
        batch.draw(tex, x, y + height, width, -height);
    }

    public void drawText(BitmapFont font, String str, int x, int y) {
        if (!rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(String str, float x, float y) {
        if (!rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(TextObject str, int x, int y) {
        if (!rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(TextObject str, float x, float y) {
        if (!rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(BitmapFont font, String str, float x, float y) {
        if (!rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str, x, y);
    }

    public void drawText(BitmapFont font, TextObject str, int x, int y) {
        if (!rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(BitmapFont font, TextObject str, float x, float y) {
        if (!rendering) return;

        font.setColor(getColor().toGdx());
        font.draw(batch, str.getText(), x, y);
    }

    public void drawText(String str, int x, int y, Anchor anchor) {
        if (!rendering) return;


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
        if (!rendering) return;


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
        if (!rendering) return;


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
        if (!rendering) return;


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
        if (!rendering) return;


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
        if (!rendering) return;


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
        if (!rendering) return;


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
        if (!rendering) return;


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
        if (!rendering) return;

        for (String line : str.split("\n"))
            drawText(line, x, y += (int) font.getLineHeight());
    }

    public void drawMultiLineText(BitmapFont font, String str, int x, int y) {
        if (!rendering) return;

        for (String line : str.split("\n"))
            drawText(font, line, x, y += (int) font.getLineHeight());
    }

    public void drawWrappedText(String text, int x, int y, int maxWidth) {
        if (!rendering) return;

        List<String> lines = StringUtils.wrap(text, font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        drawMultiLineText(joined, x, y);
    }

    public void drawWrappedText(BitmapFont font, String text, int x, int y, int maxWidth) {
        if (!rendering) return;

        List<String> lines = StringUtils.wrap(text, font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        drawMultiLineText(font, joined, x, y);
    }

    public void drawWrappedText(TextObject text, int x, int y, int maxWidth) {
        if (!rendering) return;

        List<String> lines = StringUtils.wrap(text.getText(), font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        drawMultiLineText(joined, x, y);
    }

    public void drawWrappedText(BitmapFont font, TextObject text, int x, int y, int maxWidth) {
        if (!rendering) return;

        List<String> lines = StringUtils.wrap(text.getText(), font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        drawMultiLineText(font, joined, x, y);
    }

    public void drawTabbedText(String str, int x, int y) {
        if (!rendering) return;

        for (String line : str.split("\t"))
            drawText(line, x += (int) font.getLineHeight(), y);
    }

    public void drawTabbedText(BitmapFont font, String str, int x, int y) {
        if (!rendering) return;

        for (String line : str.split("\t"))
            drawText(font, line, x += (int) font.getLineHeight(), y);
    }

    public void drawCenteredText(String text, float x, float y) {
        if (!rendering) return;

        GraphicsUtils.drawCenteredString(this, text, new Vector2(x, y), this.font);
    }

    public void drawCenteredText(TextObject text, float x, float y) {
        if (!rendering) return;

        GraphicsUtils.drawCenteredString(this, text.getText(), new Vector2(x, y), this.font);
    }

    public void drawCenteredText(BitmapFont font, String text, float x, float y) {
        if (!rendering) return;

        GraphicsUtils.drawCenteredString(this, text, new Vector2(x, y), font);
    }

    public void drawLeftAnchoredText(String text, float x, float y) {
        if (!rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text, new Vector2(x, y), 0, this.font);
    }

    public void drawLeftAnchoredText(TextObject text, float x, float y) {
        if (!rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text.getText(), new Vector2(x, y), 0, this.font);
    }

    public void drawLeftAnchoredText(BitmapFont font, String text, float x, float y) {
        if (!rendering) return;

        GraphicsUtils.drawLeftAnchoredString(this, text, new Vector2(x, y), 0, font);
    }

    public void drawRightAnchoredText(String text, float x, float y) {
        if (!rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text, new Vector2(x, y), 0, this.font);
    }

    public void drawRightAnchoredText(TextObject text, float x, float y) {
        if (!rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text.getText(), new Vector2(x, y), 0, this.font);
    }

    public void drawRightAnchoredText(BitmapFont font, String text, float x, float y) {
        if (!rendering) return;

        GraphicsUtils.drawRightAnchoredString(this, text, new Vector2(x, y), 0, font);
    }

    public void clear() {
        if (!rendering) return;

        ScreenUtils.clear(clearColor.toGdx(), true);
    }

    ////////////////////////////
    //     Transformation     //
    ////////////////////////////
    public void translate(float x, float y) {
        if (!rendering) return;

        this.editMatrix(m -> m.translate(x, y, 0));
    }

    private void editMatrix(Function<Matrix4, Matrix4> editor) {
        Matrix4 m = this.batch.getTransformMatrix();
        this.batch.setTransformMatrix(editor.apply(m));
    }

    public void translate(int x, int y) {
        if (!rendering) return;

        this.editMatrix(m -> m.translate(x, y, 0));
    }

    public void translate(float x, float y, float z) {
        if (!rendering) return;

        this.editMatrix(m -> m.translate(x, y, z));
    }

    public void translate(int x, int y, int z) {
        if (!rendering) return;

        this.editMatrix(m -> m.translate(x, y, z));
    }

    public void rotate(float theta) {
        if (!rendering) return;

        float halfTheta = theta / 2;
        float sinHalfTheta = (float) Math.sin(halfTheta);
        this.editMatrix(m -> m.rotate(new Quaternion(0, 0, sinHalfTheta, (float) Math.cos(halfTheta))));
    }

    public void rotate(double theta, double x, double y) {
        if (!rendering) return;

        this.editMatrix(m -> m.rotate(new Quaternion(1, 0, 0, (float) x)).rotate(new Quaternion(0, 1, 0, (float) y)));
    }

    @Deprecated(forRemoval = true)
    public void scale(double sx, double sy) {
        if (!rendering) return;

        this.editMatrix(m -> m.scale((float) sx, (float) sy, 0));
    }

    public void scale(float sx, float sy) {
        if (!rendering) return;

        this.editMatrix(m -> m.scale(sx, sy, 0));
    }

    public void scale(float sx, float sy, float sz) {
        if (!rendering) return;

        this.editMatrix(m -> m.scale(sx, sy, sz));
    }

    public void subInstance(Rectangle rectangle, Consumer<Renderer> consumer) {
        if (!rendering) return;

        this.subInstance((int) rectangle.x, (int) rectangle.y, (int) rectangle.width, (int) rectangle.height, consumer);
    }

    public void subInstance(int x, int y, int width, int height, Consumer<Renderer> consumer) {
        if (!rendering) return;

//        this.pushMatrix();
//        this.translate(x, y);
//        this.pushScissors(x, y, width, height);
        consumer.accept(this);
        this.game.notifications.notifyOnce(
                UUID.fromString("ef14bc6a-9c08-4233-a69a-28b87b01d739"),
                new Notification("Error! (SubInstance Fail)", "Broken game rendering may occur", "Rendering System", Duration.ofSeconds(10))
        );
//        this.popScissors();
//        this.popMatrix();
    }

    public void pushScissorsRaw(int x, int y, int width, int height) {
        if (!rendering) return;

        ScissorStack.pushScissors(new Rectangle(x, y, width, height));
    }

    public void pushScissors(int x, int y, int width, int height) {
        if (!rendering) return;

        ScissorStack.pushScissors(new Rectangle(x, y, width, height));
    }

    public void pushScissors(float x, float y, float width, float height) {
        if (!rendering) return;

        ScissorStack.pushScissors(new Rectangle(x, y, width, height));
    }

    public void popScissors() {
        if (!rendering) return;

        ScissorStack.popScissors();
    }

    @ApiStatus.Experimental
    public void clearScissors() {
        if (!rendering) return;

        while (ScissorStack.peekScissors() != null) {
            ScissorStack.popScissors();
        }
    }

    @ApiStatus.Experimental
    public void clearMatrixStack() {
        if (!rendering) return;

        while (matrixStack.peek() != null) {
            matrixStack.pop();
        }
    }

    public void pushMatrix() {
        if (!rendering) return;

//        Matrix4 matrix = this.batch.getTransformMatrix();
//        this.matrixStack.push(matrix);
//        this.batch.setTransformMatrix(matrix.cpy());
    }

    public void popMatrix() {
        if (!rendering) return;

//        if (matrixStack.isEmpty()) throw new IllegalStateException("Matrix stack is already empty");
//
//        Matrix4 matrix = this.matrixStack.pop();
//        this.batch.setTransformMatrix(matrix);
    }

    public boolean hitClip(int x, int y, int width, int height) {
        return false;
    }

    ///////////////////////
    //     To String     //
    ///////////////////////
    @Override
    public String toString() {
        return "Renderer{" +
                "matrixStack=" + matrixStack +
                '}';
    }

    @Deprecated
    public void drawEffectBox(int x, int y, int width, int height) {
        if (!rendering) return;

        drawEffectBox(x, y, width, height, new Insets(2, 2, 2, 2));
    }

    @Deprecated
    @ApiStatus.Experimental
    public void drawEffectBox(int x, int y, int width, int height, Insets insets) {
        if (!rendering) return;

        drawEffectBox(x, y, width, height, insets, 10);
    }

    @Deprecated
    @ApiStatus.Experimental
    public void drawEffectBox(int x, int y, int width, int height, Insets insets, int speed) {
        if (!rendering) return;

        setColor(Color.rgb(0x00d0d0));
        setLineWidth(insets.top == insets.bottom && insets.bottom == insets.left && insets.left == insets.right ? insets.left : Math.max(Math.max(Math.max(insets.top, insets.bottom), insets.left), insets.right));
        rectLine(x, y, width, height);
    }

    @Deprecated
    public void drawEffectBox(int x, int y, int width, int height, float strokeWidth) {
        if (!rendering) return;

        drawEffectBox(x, y, width, height, strokeWidth, 10);
    }

    @Deprecated
    public void drawEffectBox(int x, int y, int width, int height, float borderWidth, int speed) {
        if (!rendering) return;

        setColor(Color.rgb(0x00a0ff));
        setLineWidth(borderWidth);
        rectLine(x, y, width, height);
//        GradientPaint p = getEffectPaint(speed);
//        Border border = new Border(insets);
//        border.setPaint(p);
//        border.paintBorder(this, x, y, width, height);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height) {
        if (!rendering) return;

        drawRoundEffectBox(x, y, width, height, 10);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height, int radius) {
        if (!rendering) return;

        drawRoundEffectBox(x, y, width, height, radius, 2);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height, int radius, int borderWidth) {
        if (!rendering) return;

        drawRoundEffectBox(x, y, width, height, radius, borderWidth, 10);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height, int radius, int borderWidth, int speed) {
        if (!rendering) return;

//        radius -= borderWidth - 1;
        setColor(Color.rgb(0x00d0d0));
        setLineWidth(borderWidth);
        roundRectLine(x, y, width, height, radius, radius);
//        paint(getEffectPaint(speed));
//        Stroke old = getStroke();
//        stroke(new BasicStroke(borderWidth));
//        roundRectLine(x, y, width, height, radius, radius);
//        stroke(old);
    }

    @Deprecated
    public void drawErrorEffectBox(int x, int y, int width, int height) {
        if (!rendering) return;

        drawErrorEffectBox(x, y, width, height, new Insets(2, 2, 2, 2));
    }

    @Deprecated
    public void drawErrorEffectBox(int x, int y, int width, int height, Insets insets) {
        if (!rendering) return;

        drawErrorEffectBox(x, y, width, height, insets, 10);
    }

    @Deprecated
    public void drawErrorEffectBox(int x, int y, int width, int height, Insets insets, int speed) {
        if (!rendering) return;

        setColor(Color.rgb(0xff3000));
        setLineWidth(insets.top == insets.bottom && insets.bottom == insets.left && insets.left == insets.right ? insets.left : Math.max(Math.max(Math.max(insets.top, insets.bottom), insets.left), insets.right));
        rectLine(x, y, width, height);
//        GradientPaint p = getErrorEffectPaint(speed);
//        Border border = new Border(insets);
//        border.setPaint(p);
//        border.paintBorder(this, x, y, width, height);
    }

    public void fillErrorEffect(int x, int y, int width, int height) {
        if (!rendering) return;

        this.fillScrollingGradient(x, y, width, height, 10, 0xff3000, 0xffa000);
    }

    public void fillErrorEffect(int x, int y, int width, int height, int speed) {
        if (!rendering) return;

        this.fillScrollingGradient(x, y, width, height, speed, 0xff3000, 0xffa000);
    }

    public void fillEffect(int x, int y, int width, int height) {
        if (!rendering) return;

        this.fillScrollingGradient(x, y, width, height, 10, 0x00a0ff, 0x00ffa0);
    }

    public void fillEffect(int x, int y, int width, int height, int speed) {
        if (!rendering) return;

        this.fillScrollingGradient(x, y, width, height, speed, 0x00a0ff, 0x00ffa0);
    }

    private void fillScrollingGradient(int x, int y, int width, int height, int speed, int color1, int color2) {
        float gameWidth = this.getWidth();
        float gameHeight = this.getHeight();
        var shiftX = (gameWidth * 2f * BubbleBlaster.getTicks() / (float) (BubbleBlaster.TPS * speed) - batch.getTransformMatrix().getTranslation(new Vector3()).x) % (gameWidth * 2);

        // Todo: do the effect!
//        GLScissorState state = GLScissorState.captureScissor();
//        Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);
//        Gdx.gl20.glScissor(x, y, Math.abs(width), Math.abs(height));
//        this.fillGradient(-shiftX, 0, gameWidth, gameHeight, Color.rgb(color1), Color.rgb(color2), Axis2D.HORIZONTAL);
//        this.fillGradient(-shiftX + gameWidth, 0, gameWidth, gameHeight, Color.rgb(color2), Color.rgb(color1), Axis2D.HORIZONTAL);
//        this.fillGradient(-shiftX + gameWidth * 2, 0, gameWidth, gameHeight, Color.rgb(color1), Color.rgb(color2), Axis2D.HORIZONTAL);
//        state.reapplyState();

        this.fillGradient(x, y, width, height, Color.rgb(color1), Color.rgb(color2), Axis2D.HORIZONTAL);
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
        if (!rendering) return;

        batch.draw(curTexture, x, y);
    }

    public void blit(int x, int y, int width, int height) {
        if (!rendering) return;

        batch.draw(curTexture, x, y, width, height);
    }

    public void blit(Identifier texture) {
        if (!rendering) return;

        this.curTexture = game.getTextureManager().getTexture(texture);
    }

    ////////////////////////
    //     Properties     //
    ////////////////////////
    public Deque<Matrix4> getMatrixStack() {
        return matrixStack;
    }

    public Matrix4 getTransform() {
        return matrixStack.peek();
    }

    public void setTransform(Matrix4 matrix) {
        if (!rendering) return;

        Matrix4 m = matrixStack.peek();
        if (m != null) {
            m.set(matrix);
            batch.setTransformMatrix(m);
        }
    }

    public void setLineWidth(float lineWidth) {
        if (!rendering) return;

        this.lineWidth = lineWidth;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setColor(Color c) {
        if (!rendering) return;
        if (c == null) return;

        font.setColor(c.toGdx());
        shapes.setColor(c.toGdx());
    }

    public void setColor(int r, int g, int b) {
        if (!rendering) return;

        setColor(Color.rgb(r, g, b));
    }

    public void setColor(float r, float g, float b) {
        if (!rendering) return;

        setColor(Color.rgb(r, g, b));
    }

    public void setColor(int r, int g, int b, int a) {
        if (!rendering) return;

        setColor(Color.rgba(r, g, b, a));
    }

    public void setColor(float r, float g, float b, float a) {
        if (!rendering) return;

        setColor(Color.rgba(r, g, b, a));
    }

    public void setColor(int argb) {
        if (!rendering) return;

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
        if (!rendering) return;

        setColor(Color.hex(hex));
    }

    public Color getColor() {
        var color = new com.badlogic.gdx.graphics.Color();
        com.badlogic.gdx.graphics.Color.abgr8888ToColor(color, shapes.getPackedColor());
        return Color.gdx(color);
    }

    public void setClearColor(Color color) {
        if (!rendering) return;

        gl20.glClearColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public void setClearColor(int red, int green, int blue) {
        if (!rendering) return;

        setClearColor(Color.rgb(red, green, blue));
    }

    public void setClearColor(float red, float green, float blue) {
        if (!rendering) return;

        setClearColor(Color.rgb(red, green, blue));
    }

    public void setClearColor(int red, int green, int blue, int alpha) {
        if (!rendering) return;

        setClearColor(Color.rgba(red, green, blue, alpha));
    }

    public void setClearColor(float red, float green, float blue, float alpha) {
        if (!rendering) return;

        setClearColor(Color.rgba(red, green, blue, alpha));
    }

    public void setClearColor(int argb) {
        if (!rendering) return;

        setClearColor(Color.argb(argb));
    }

    public void setClearColor(String hex) {
        if (!rendering) return;

        setClearColor(Color.hex(hex));
    }

    public Color getClearColor() {
        return clearColor;
    }

    public void setFont(BitmapFont font) {
        if (!rendering) return;

        this.font = font;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void roundedLine(float x1, float y1, float x2, float y2) {
        shapes.path(Array.with(new Vector2(x1, y1), new Vector2(x2, y2)), lineWidth, JoinType.SMOOTH, false);
    }

    public enum State {
        BATCH, SHAPES
    }
}
