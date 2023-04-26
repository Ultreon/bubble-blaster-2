/////////////////////
//     Package     //
/////////////////////
package com.ultreon.bubbles.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.vector.Vec2d;
import com.ultreon.bubbles.vector.Vec4i;
import com.ultreon.commons.util.StringUtils;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.text.v0.TextObject;
import org.jetbrains.annotations.NotNull;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Renderer class.
 *
 * @author Qboi
 * @see Graphics
 * @see Graphics2D
 * @see Font
 * @see GraphicsConfiguration
 * @see FontRenderContext
 * @see FontMetrics
 * @see Color
 * @see Paint
 * @see Composite
 * @see Stroke
 * @see String
 * @see ImageObserver
 * @see RenderingHints
 * @see Shape
 * @see AffineTransform
 * @see AttributedCharacterIterator
 * @see GlyphVector
 * @see Polygon
 * @see Rectangle
 */
@SuppressWarnings("unused")
public class Renderer {
    ////////////////////
    //     Fields     //
    ////////////////////
    private State state;
    private final BubbleBlaster game = BubbleBlaster.getInstance();
//    private Texture curTexture;
    private Vec2d globalTranslation = new Vec2d();
    private final GL20 gl20;
    private final GL30 gl30;
    private final Batch batch;
    private final ShapeDrawer shapes;
    private float strokeWidth;
    private Texture curTexture;
    private BitmapFont font;
    private MatrixStack matrixStack;

    //////////////////////////
    //     Constructors     //
    //////////////////////////
    public Renderer(ShapeDrawer shapes) {
        this(shapes, new MatrixStack());
    }

    //////////////////////////
    //     Constructors     //
    //////////////////////////
    public Renderer(ShapeDrawer shapes, MatrixStack matrixStack) {
        this.font = game.getBitmapFont();
        this.gl20 = Gdx.gl20;
        this.gl30 = Gdx.gl30;
        this.batch = shapes.getBatch();
        this.shapes = shapes;
        this.matrixStack = matrixStack;

        // Projection matrix.
        Consumer<Matrix4> projectionMatrixSetter = matrix -> {
            shapes.getBatch().setTransformMatrix(matrix);
        };
        this.matrixStack.onPush = projectionMatrixSetter;
        this.matrixStack.onPop = projectionMatrixSetter;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Renderer(Graphics gfx, ImageObserver observer) {
        this(null);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Renderer(Graphics2D gfx2d, ImageObserver observer) {
        this(null);
    }
    ////////////////////////
    //     Properties     //
    ////////////////////////

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void composite(Composite comp) {
//        gfx.setComposite(comp);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void paint(Paint paint) {
//        gfx.setPaint(paint);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void stroke(Stroke s) {
        if (s instanceof BasicStroke basicStroke) {
            this.strokeWidth = basicStroke.getLineWidth();
        }
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setColor(Color c) {
        if (c == null) return;
//        switchToBatch();
//        batch.setColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
        shapes.setColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
    }

    public void setColor(int r, int g, int b) {
        setColor(Color.rgb(r, g, b));
    }

    public void setColor(float r, float g, float b) {
        setColor(Color.rgb(r, g, b));
    }

    public void setColor(int r, int g, int b, int a) {
        setColor(Color.rgba(r, g, b, a));
    }

    public void setColor(float r, float g, float b, float a) {
        setColor(Color.rgba(r, g, b, a));
    }

    public void setColor(int argb) {
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
        setColor(Color.hex(hex));
    }

    public void clearColor(Color color) {
        gl20.glClearColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public void clearColor(int red, int green, int blue) {
        clearColor(Color.rgb(red, green, blue));
    }

    public void clearColor(float red, float green, float blue) {
        clearColor(Color.rgb(red, green, blue));
    }

    public void clearColor(int red, int green, int blue, int alpha) {
        clearColor(Color.rgba(red, green, blue, alpha));
    }

    public void clearColor(float red, float green, float blue, float alpha) {
        clearColor(Color.rgba(red, green, blue, alpha));
    }

    public void clearColor(int argb) {
        clearColor(Color.argb(argb));
    }

    public void clearColor(String hex) {
        clearColor(Color.hex(hex));
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void paintMode() {
//        gfx.setPaintMode();
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void xorMode(Color c1) {
//        gfx.setXORMode(c1.toAwt());
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void xorMode(int red, int green, int blue) {
        xorMode(Color.rgb(red, green, blue));
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void xorMode(float red, float green, float blue) {
        xorMode(Color.rgb(red, green, blue));
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void xorMode(int red, int green, int blue, int alpha) {
        xorMode(Color.rgba(red, green, blue, alpha));
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void xorMode(float red, float green, float blue, float alpha) {
        xorMode(Color.rgba(red, green, blue, alpha));
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void xorMode(int argb) {
        xorMode(Color.argb(argb));
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void xorMode(String hex) {
        xorMode(Color.hex(hex));
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void hint(RenderingHints.Key hintKey, Object hintValue) {
//        gfx.setRenderingHint(hintKey, hintValue);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void hints(Map<?, ?> hints) {
//        gfx.setRenderingHints(hints);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void addHints(Map<?, ?> hints) {
//        gfx.addRenderingHints(hints);
    }

    ////////////////////
    //     Shapes     //
    ////////////////////
    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void outline(Shape s) {
        if (s instanceof Ellipse2D ellipse) {
            outline(ellipse);
        } else if (s instanceof Rectangle2D rect) {
            outline(rect);
        } else if (s instanceof Line2D rect) {
            outline(rect);
        }
    }

    public void outline(Rectangle2D rect) {
        rectLine((float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float) rect.getHeight());
    }

    public void outline(Ellipse2D ellipse) {
        ovalLine((float) ellipse.getX(), (float) ellipse.getY(), (float) ellipse.getWidth(), (float) ellipse.getHeight());
    }

    public void outline(Line2D s) {
        line((float) s.getX1(), (float) s.getY1(), (float) s.getX2(), (float) s.getY2());
    }

    public void circle(float x, float y, float radius) {
        y = game.getHeight() + radius - y;
        shapes.filledCircle(x, y, radius);
    }

    public void circleLine(float x, float y, float radius) {
        y = game.getHeight() + radius - y;
        shapes.circle(x, y, radius);
    }

    public void fill(Shape s) {
        if (s instanceof Ellipse2D ellipse) fill(ellipse);
        else if (s instanceof Rectangle2D rect) fill(rect);
        else if (s instanceof Line2D rect) fill(rect);
    }

    public void fill(Rectangle2D rect) {
        rect((float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float) rect.getHeight());
    }

    public void fill(Ellipse2D ellipse) {
        oval((float) ellipse.getX(), (float) ellipse.getY(), (float) ellipse.getWidth(), (float) ellipse.getHeight());
    }

    public void fill(Line2D line) {
        line((float) line.getX1(), (float) line.getY1(), (float) line.getX2(), (float) line.getY2());
    }

    public void fill(com.ultreon.bubbles.render.gui.widget.Rectangle r) {
        rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public void fill(Vec4i r) {
        rect(r.x, r.y, r.z, r.w);
    }

    public void line(int x1, int y1, int x2, int y2) {
        y1 = game.getHeight() - y1;
        y2 = game.getHeight() - y2;
        shapes.line(x1, y1, x2, y2);
    }

    public void line(float x1, float y1, float x2, float y2) {
        y1 = game.getHeight() - y1;
        y2 = game.getHeight() - y2;
        shapes.line(x1, y1, x2, y2);
    }

    public void rectLine(int x, int y, int width, int height) {
        y = game.getHeight() - y;
        height = -height;
        shapes.rectangle(x, y, width, height, strokeWidth);
    }

    public void rectLine(float x, float y, float width, float height) {
        y = game.getHeight() - y;
        height = -height;
        shapes.rectangle(x, y, width, height, strokeWidth);
    }

    public void rect(int x, int y, int width, int height) {
        y = game.getHeight() - y;
        height = -height;
        shapes.filledRectangle(x, y, width, height);
    }

    public void rect(float x, float y, float width, float height) {
        y = game.getHeight() - y;
        height = -height;
        shapes.filledRectangle(x, y, width, height);
    }

    public void roundRectLine(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        y = game.getHeight() - y;
        height = -height;
        shapes.rectangle(x, y, width, height, strokeWidth);
    }

    public void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        y = game.getHeight() - y;
        height = -height;
        shapes.rectangle(x, y, width, height);
    }

    public void rect3DLine(int x, int y, int width, int height, boolean raised) {
        y = game.getHeight() - y;
        height = -height;
        shapes.rectangle(x, y, width, height, strokeWidth);
    }

    public void rect3D(int x, int y, int width, int height, boolean raised) {
        y = game.getHeight() - y;
        height = -height;
        shapes.filledRectangle(x, y, width, height);
    }

    public void ovalLine(int x, int y, int width, int height) {
        y = game.getHeight() - y;
        height = -height;
        shapes.ellipse(x, y, width, height);
    }

    public void oval(int x, int y, int width, int height) {
        y = game.getHeight() - y;
        height = -height;
        shapes.filledEllipse(x, y, width, height);
    }

    public void ovalLine(float x, float y, float width, float height) {
        y = game.getHeight() - y;
        height = -height;
        shapes.ellipse(x, y, width, height);
    }

    public void oval(float x, float y, float width, float height) {
        y = game.getHeight() - y;
        height = -height;
        shapes.filledEllipse(x, y, width, height);
    }

    public void arcLine(int x, int y, int width, int height, int startAngle, int arcAngle) {
        y = game.getHeight() - y;
        height = -height;
        shapes.arc(x, y, width, startAngle, arcAngle);
    }

    public void arc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        y = game.getHeight() - y;
        height = -height;
        shapes.arc(x, y, width, startAngle, arcAngle);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void polyline(int[] xPoints, int[] yPoints, int nPoints) {
//        gfx.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void polygonLine(int[] xPoints, int[] yPoints, int nPoints) {
//        gfx.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void polygonLine(Polygon p) {
//        gfx.drawPolygon(p);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void polygon(int[] xPoints, int[] yPoints, int nPoints) {
//        gfx.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void polygon(Polygon p) {
//        gfx.fillPolygon(p);
    }

    ///////////////////
    //     Image     //
    ///////////////////
    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public boolean image(Image img, int x, int y) {
        return false;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public boolean image(Image img, int x, int y, int width, int height) {
        return false;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public boolean image(Image img, int x, int y, Color backgroundColor) {
        return false;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public boolean image(Image img, int x, int y, int width, int height, Color backgroundColor) {
        return false;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public boolean image(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
        return false;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public boolean image(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color backgroundColor) {
        return false;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public boolean image(Image img, AffineTransform xForm) {
        return false;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void image(BufferedImage img, BufferedImageOp op, int x, int y) {

    }

    public void texture(Texture tex, float x, float y) {
        y = game.getHeight() - y - tex.getHeight();
//        shapes.setTextureRegion(new TextureRegion(tex, tex.getWidth(), tex.getHeight()));
//        rect(x, y, tex.getWidth(), tex.getHeight());
//        shapes.setTextureRegion(null);
        batch.draw(tex, x, y);
    }

    public void texture(Texture tex, float x, float y, Color backgroundColor) {
        y = game.getHeight() - y - tex.getHeight();
        setColor(backgroundColor);
        rect(x, y, tex.getWidth(), tex.getHeight());
        batch.draw(tex, x, y);
    }

    public void texture(Texture tex, float x, float y, float width, float height) {
        y = game.getHeight() - y - height;
        batch.draw(tex, x, y, width, height);
    }

    public void texture(Texture tex, float x, float y, float width, float height, Color backgroundColor) {
        y = game.getHeight() - y - height;
        setColor(backgroundColor);
        rect(x, y, width, height);
        batch.draw(tex, x, y, width, height);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void renderedImage(RenderedImage img, AffineTransform xForm) {

    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void renderableImage(RenderableImage img, AffineTransform xForm) {
         
    }

    //////////////////
    //     Text     //
    //////////////////
    public void text(String str, int x, int y) {
        y = game.getHeight() - y;
        font.draw(batch, str, x, y);
    }

    public void text(String str, float x, float y) {
        y = game.getHeight() - y;
        font.draw(batch, str, x, y);
    }

    public void text(TextObject str, int x, int y) {
        y = game.getHeight() - y;
        font.draw(batch, str.getText(), x, y);
    }

    public void text(TextObject str, float x, float y) {
        y = game.getHeight() - y;
        font.draw(batch, str.getText(), x, y);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void text(AttributedCharacterIterator iterator, int x, int y) {
        y = game.getHeight() - y;
        font.draw(batch, iterator.toString(), x, y);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void text(AttributedCharacterIterator iterator, float x, float y) {
        y = game.getHeight() - y;
        font.draw(batch, iterator.toString(), x, y);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void text(AttributedString iterator, int x, int y) {
        y = game.getHeight() - y;
        font.draw(batch, iterator.toString(), x, y);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void text(AttributedString iterator, float x, float y) {
        y = game.getHeight() - y;
        font.draw(batch, iterator.toString(), x, y);
    }

    public void multiLineText(String str, int x, int y) {
        y -= font.getLineHeight();

        for (String line : str.split("\n"))
            text(line, x, y += font.getLineHeight());
    }

    public void wrappedText(String str, int x, int y, int maxWidth) {
        List<String> lines = StringUtils.wrap(str, font, new GlyphLayout(), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        multiLineText(joined, x, y);
    }

    public void tabString(String str, int x, int y) {
        for (String line : str.split("\t"))
            text(line, x += font.getLineHeight(), y);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void chars(char[] data, int offset, int length, int x, int y) {

    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void bytes(byte[] data, int offset, int length, int x, int y) {

    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void font(Font font) {
//        gfx.setFont(font);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void fallbackFont(BitmapFont font) {
//        fallbackFont = font;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void clearRect(int x, int y, int width, int height) {
        clear();
    }

    public void clear() {
        gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void glyphVectorLine(GlyphVector g, float x, float y) {

    }

    ////////////////////////////
    //     Transformation     //
    ////////////////////////////
    public void translate(double tx, double ty) {
        globalTranslation.add(tx, ty);
        matrixStack.translate(tx, ty);
    }

    public void translate(int x, int y) {
        globalTranslation.add(x, y);
        matrixStack.translate((float) x, (float) y);
    }

    public void rotate(double theta) {
        matrixStack.rotate(new Quaternion(1, 0, 0, (float) theta));
    }

    public void rotate(double theta, double x, double y) {
        matrixStack.rotate(new Quaternion(1, 0, 0, (float) x));
        matrixStack.rotate(new Quaternion(0, 1, 0, (float) y));
    }

    public void scale(double sx, double sy) {
        matrixStack.scale((float) sx, (float) sy);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void shear(double shx, double shy) {
        
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void transform(AffineTransform Tx) {
        
    }

    public void clip(Shape s) {
        
    }

    public void clipRect(int x, int y, int width, int height) {
        
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
//        gfx.copyArea(x, y, width, height, dx, dy);
    }

    /////////////////////
    //     Setters     //
    /////////////////////
    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void setTransform(AffineTransform Tx) {
        
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void simpleClip(int x, int y, int width, int height) {
        
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public void simpleClip(Shape clip) {
        
    }

    /////////////////////
    //     Getters     //
    /////////////////////
    public Matrix4 getTransform() {
        return matrixStack.last();
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Paint getPaint() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Composite getComposite() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Color getClearColor() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Stroke getStroke() {
        return null;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public Color getColor() {
        var color = new com.badlogic.gdx.graphics.Color();
        com.badlogic.gdx.graphics.Color.abgr8888ToColor(color, shapes.getPackedColor());
        return Color.gdx(color);
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public BitmapFont getFallbackFont() {
        return null;
    }

    public BitmapFont getFont() {
        return font;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public FontMetrics fontMetrics(Font f) {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Shape getClip() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Rectangle getClipBounds() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Rectangle getClipBounds(Rectangle r) {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public FontMetrics fontMetrics() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public FontRenderContext getFontRenderContext() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public GraphicsConfiguration getDeviceConfiguration() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public RenderingHints getRenderingHints() {
        return new RenderingHints(Map.of());
    }

    ///////////////////////////
    //     Miscellaneous     //
    ///////////////////////////
    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Renderer subInstance() {
        return new Renderer(shapes, new MatrixStack(new Matrix4(matrixStack.last())));
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Renderer subInstance(int x, int y, int width, int height) {
        return this;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Renderer subInstance(com.ultreon.bubbles.render.gui.widget.Rectangle bounds) {
        return subInstance(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void subInstance(int x, int y, int width, int height, Consumer<Renderer> consumer) {
        ScissorStack.pushScissors(new com.badlogic.gdx.math.Rectangle(x, y, width, height));
        matrixStack.push();
        matrixStack.translate(x, y);

        consumer.accept(this);

        matrixStack.pop();
    }

    public boolean hitClip(int x, int y, int width, int height) {
        return false;
    }

    public void dispose() {

    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
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

    public void drawEffectBox(int x, int y, int width, int height) {
        drawEffectBox(x, y, width, height, new Insets(2, 2, 2, 2));
    }

    @Deprecated
    public void drawEffectBox(int x, int y, int width, int height, Insets insets) {
        drawEffectBox(x, y, width, height, insets, 10);
    }

    @Deprecated
    public void drawEffectBox(int x, int y, int width, int height, Insets insets, int speed) {
        setColor(Color.rgb(0x00a0ff));
        setStrokeWidth(insets.top == insets.bottom && insets.bottom == insets.left && insets.left == insets.right ? insets.left : Math.max(Math.max(Math.max(insets.top, insets.bottom), insets.left), insets.right));
        rect(x, y, width, height);
//        GradientPaint p = getEffectPaint(speed);
//        Border border = new Border(insets);
//        border.setPaint(p);
//        border.paintBorder(this, x, y, width, height);
    }

    public void drawEffectBox(int x, int y, int width, int height, float strokeWidth) {
        drawEffectBox(x, y, width, height, strokeWidth, 10);
    }

    public void drawEffectBox(int x, int y, int width, int height, float borderWidth, int speed) {
        setColor(Color.rgb(0x00a0ff));
        setStrokeWidth(borderWidth);
        rectLine(x, y, width, height);
//        GradientPaint p = getEffectPaint(speed);
//        Border border = new Border(insets);
//        border.setPaint(p);
//        border.paintBorder(this, x, y, width, height);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height) {
        drawRoundEffectBox(x, y, width, height, 10);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height, int radius) {
        drawRoundEffectBox(x, y, width, height, radius, 2);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height, int radius, int borderWidth) {
        drawRoundEffectBox(x, y, width, height, radius, borderWidth, 10);
    }

    public void drawRoundEffectBox(int x, int y, int width, int height, int radius, int borderWidth, int speed) {
//        radius -= borderWidth - 1;
        setColor(Color.rgb(0x00a0ff));
        setStrokeWidth(borderWidth);
        rectLine(x, y, width, height);
//        paint(getEffectPaint(speed));
//        Stroke old = getStroke();
//        stroke(new BasicStroke(borderWidth));
//        roundRectLine(x, y, width, height, radius, radius);
//        stroke(old);
    }

    public void drawErrorEffectBox(int x, int y, int width, int height) {
        drawErrorEffectBox(x, y, width, height, new Insets(2, 2, 2, 2));
    }

    public void drawErrorEffectBox(int x, int y, int width, int height, Insets insets) {
        drawErrorEffectBox(x, y, width, height, insets, 10);
    }

    public void drawErrorEffectBox(int x, int y, int width, int height, Insets insets, int speed) {
        setColor(Color.rgb(0xff3000));
        setStrokeWidth(insets.top == insets.bottom && insets.bottom == insets.left && insets.left == insets.right ? insets.left : Math.max(Math.max(Math.max(insets.top, insets.bottom), insets.left), insets.right));
        rectLine(x, y, width, height);
//        GradientPaint p = getErrorEffectPaint(speed);
//        Border border = new Border(insets);
//        border.setPaint(p);
//        border.paintBorder(this, x, y, width, height);
    }

    public void fillEffect(int x, int y, int width, int height) {
        fillEffect(x, y, width, height, 10);
    }

    public void fillEffect(int x, int y, int width, int height, int speed) {
        setColor(Color.rgb(0x00a0ff));
        rect(x, y, width, height);
//        GradientPaint p = getEffectPaint(speed);
//        Paint old = getPaint();
//        paint(p);
//        rect(x, y, width, height);
//        paint(old);
    }

    @NotNull
    private GradientPaint getEffectPaint(int speed) {
        return getEffectPaint(speed, 0x00a0ff, 0x00ffa0);
    }

    @NotNull
    private GradientPaint getErrorEffectPaint(int speed) {
        return getEffectPaint(speed, 0xff3000, 0xffa000);
    }

    @NotNull
    private GradientPaint getEffectPaint(int speed, int color1, int color2) {
        var width = game.getScaledWidth();
        var shiftX = (((double) width * 2) * BubbleBlaster.getTicks() / (double)(BubbleBlaster.TPS * speed)) - globalTranslation.x;
        return new GradientPaint((float) shiftX - width, 0, Color.rgb(color1).toAwt(), (float) shiftX, 0f, Color.rgb(color2).toAwt(), true);
    }

    public void blit(int x, int y) {
        batch.draw(curTexture, x, y);
    }

    public void blit(int x, int y, int width, int height) {
        batch.draw(curTexture, x, y, width, height);
    }

    public void texture(Identifier texture) {
        this.curTexture = game.getTextureManager().getTexture(texture);
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public State getState() {
        return state;
    }

    public enum State {
        BATCH, SHAPES
    }
}
