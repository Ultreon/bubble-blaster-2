/////////////////////
//     Package     //
/////////////////////
package com.ultreon.bubbles.render;

import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.gui.border.Border;
import com.ultreon.bubbles.vector.Vec4i;
import com.ultreon.commons.util.StringUtils;

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
import java.util.List;
import java.util.Map;

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
    Font fallbackFont;
    final Graphics2D gfx;
    final ImageObserver observer;
    private RenderState state;

    //////////////////////////
    //     Constructors     //
    //////////////////////////
    public Renderer(Graphics gfx, ImageObserver observer) {
        this.gfx = (Graphics2D) gfx;
        this.observer = observer;
    }

    public Renderer(Graphics2D gfx2d, ImageObserver observer) {
        this.gfx = gfx2d;
        this.observer = observer;
    }

    public Renderer(Renderer renderer) {
        this.fallbackFont = renderer.fallbackFont;
        this.gfx = renderer.gfx;
        this.state = renderer.state;
        this.observer = renderer.observer;
    }

    ////////////////////////
    //     Properties     //
    ////////////////////////
    public void composite(Composite comp) {
        gfx.setComposite(comp);
    }

    public void paint(Paint paint) {
        gfx.setPaint(paint);
    }

    public void stroke(Stroke s) {
        gfx.setStroke(s);
    }

    public void color(Color c) {
        gfx.setColor(c == null ? null : c.toAwt());
    }

    public void color(int r, int g, int b) {
        color(Color.rgb(r, g, b));
    }

    public void color(float r, float g, float b) {
        color(Color.rgb(r, g, b));
    }

    public void color(int r, int g, int b, int a) {
        color(Color.rgba(r, g, b, a));
    }

    public void color(float r, float g, float b, float a) {
        color(Color.rgba(r, g, b, a));
    }

    public void color(int argb) {
        color(Color.argb(argb));
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
    public void color(String hex) {
        color(Color.hex(hex));
    }

    public void clearColor(Color color) {
        gfx.setBackground(color.toAwt());
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

    public void paintMode() {
        gfx.setPaintMode();
    }

    public void xorMode(Color c1) {
        gfx.setXORMode(c1.toAwt());
    }

    public void xorMode(int red, int green, int blue) {
        xorMode(Color.rgb(red, green, blue));
    }

    public void xorMode(float red, float green, float blue) {
        xorMode(Color.rgb(red, green, blue));
    }

    public void xorMode(int red, int green, int blue, int alpha) {
        xorMode(Color.rgba(red, green, blue, alpha));
    }

    public void xorMode(float red, float green, float blue, float alpha) {
        xorMode(Color.rgba(red, green, blue, alpha));
    }

    public void xorMode(int argb) {
        xorMode(Color.argb(argb));
    }

    public void xorMode(String hex) {
        xorMode(Color.hex(hex));
    }

    public void hint(RenderingHints.Key hintKey, Object hintValue) {
        gfx.setRenderingHint(hintKey, hintValue);
    }

    public void hints(Map<?, ?> hints) {
        gfx.setRenderingHints(hints);
    }

    public void addHints(Map<?, ?> hints) {
        gfx.addRenderingHints(hints);
    }

    ////////////////////
    //     Shapes     //
    ////////////////////
    public void outline(Shape s) {
        gfx.draw(s);
    }

    public void outline(Rectangle2D s) {
        gfx.drawRect((int) s.getX(), (int) s.getY(), (int) s.getWidth(), (int) s.getHeight());
    }

    public void outline(Ellipse2D s) {
        gfx.drawOval((int) s.getX(), (int) s.getY(), (int) s.getWidth(), (int) s.getHeight());
    }

    public void outline(Line2D s) {
        gfx.drawLine((int) s.getX1(), (int) s.getY1(), (int) s.getX2(), (int) s.getY2());
    }

    public void fill(Shape s) {
        gfx.fill(s);
    }

    public void fill(Rectangle2D rect) {
        gfx.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    public void fill(Ellipse2D ellipse) {
        gfx.fillOval((int) ellipse.getX(), (int) ellipse.getY(), (int) ellipse.getWidth(), (int) ellipse.getHeight());
    }

    public void fill(Line2D line) {
        gfx.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
    }

    public void fill(com.ultreon.bubbles.render.gui.widget.Rectangle r) {
        gfx.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public void fill(Vec4i r) {
        gfx.fillRect(r.x, r.y, r.z, r.w);
    }

    public void line(int x1, int y1, int x2, int y2) {
        gfx.drawLine(x1, y1, x2, y2);
    }

    public void rectLine(int x, int y, int width, int height) {
        gfx.drawRect(x, y, width, height);
    }

    public void rect(int x, int y, int width, int height) {
        gfx.fillRect(x, y, width, height);
    }

    public void roundRectLine(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        gfx.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        gfx.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void rect3DLine(int x, int y, int width, int height, boolean raised) {
        gfx.draw3DRect(x, y, width, height, raised);
    }

    public void rect3D(int x, int y, int width, int height, boolean raised) {
        gfx.fill3DRect(x, y, width, height, raised);
    }

    public void ovalLine(int x, int y, int width, int height) {
        gfx.drawOval(x, y, width, height);
    }

    public void oval(int x, int y, int width, int height) {
        gfx.fillOval(x, y, width, height);
    }

    public void arcLine(int x, int y, int width, int height, int startAngle, int arcAngle) {
        gfx.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public void arc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        gfx.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void polyline(int[] xPoints, int[] yPoints, int nPoints) {
        gfx.drawPolyline(xPoints, yPoints, nPoints);
    }

    public void polygonLine(int[] xPoints, int[] yPoints, int nPoints) {
        gfx.drawPolygon(xPoints, yPoints, nPoints);
    }

    public void polygonLine(Polygon p) {
        gfx.drawPolygon(p);
    }

    public void polygon(int[] xPoints, int[] yPoints, int nPoints) {
        gfx.fillPolygon(xPoints, yPoints, nPoints);
    }

    public void polygon(Polygon p) {
        gfx.fillPolygon(p);
    }

    ///////////////////
    //     Image     //
    ///////////////////
    public boolean image(Image img, int x, int y) {
        return gfx.drawImage(img, x, y, observer);
    }

    public boolean image(Image img, int x, int y, int width, int height) {
        return gfx.drawImage(img, x, y, width, height, observer);
    }

    public boolean image(Image img, int x, int y, Color backgroundColor) {
        return gfx.drawImage(img, x, y, backgroundColor.toAwt(), observer);
    }

    public boolean image(Image img, int x, int y, int width, int height, Color backgroundColor) {
        return gfx.drawImage(img, x, y, width, height, backgroundColor.toAwt(), observer);
    }

    public boolean image(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
        return gfx.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    public boolean image(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color backgroundColor) {
        return gfx.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, backgroundColor.toAwt(), observer);
    }

    public boolean image(Image img, AffineTransform xForm) {
        return gfx.drawImage(img, xForm, observer);
    }

    public void image(BufferedImage img, BufferedImageOp op, int x, int y) {
        gfx.drawImage(img, op, x, y);
    }

    public void renderedImage(RenderedImage img, AffineTransform xForm) {
        gfx.drawRenderedImage(img, xForm);
    }

    public void renderableImage(RenderableImage img, AffineTransform xForm) {
        gfx.drawRenderableImage(img, xForm);
    }

    //////////////////
    //     Text     //
    //////////////////
    public void text(String str, int x, int y) {
        gfx.drawString(StringUtils.createFallbackString(str, getFont()).getIterator(), x, y);
    }

    public void text(String str, float x, float y) {
        gfx.drawString(StringUtils.createFallbackString(str, getFont()).getIterator(), x, y);
    }

    public void text(TextObject str, int x, int y) {
        gfx.drawString(StringUtils.createFallbackString(str.getText(), getFont()).getIterator(), x, y);
    }

    public void text(TextObject str, float x, float y) {
        gfx.drawString(StringUtils.createFallbackString(str.getText(), getFont()).getIterator(), x, y);
    }

    public void text(AttributedCharacterIterator iterator, int x, int y) {
        gfx.drawString(iterator, x, y);
    }

    public void text(AttributedCharacterIterator iterator, float x, float y) {
        gfx.drawString(iterator, x, y);
    }

    public void multiLineText(String str, int x, int y) {
        y -= gfx.getFontMetrics().getHeight();

        for (String line : str.split("\n"))
            text(line, x, y += gfx.getFontMetrics().getHeight());
    }

    public void wrappedText(String str, int x, int y, int maxWidth) {
        List<String> lines = StringUtils.wrap(str, fontMetrics(getFont()), maxWidth);
        String joined = org.apache.commons.lang3.StringUtils.join(lines.toArray(new String[]{}), '\n');
        multiLineText(joined, x, y);
    }

    public void tabString(String str, int x, int y) {
        for (String line : str.split("\t"))
            text(line, x += gfx.getFontMetrics().getHeight(), y);
    }

    public void chars(char[] data, int offset, int length, int x, int y) {
        gfx.drawChars(data, offset, length, x, y);
    }

    public void bytes(byte[] data, int offset, int length, int x, int y) {
        gfx.drawBytes(data, offset, length, x, y);
    }

    public void font(Font font) {
        gfx.setFont(font);
    }

    public void fallbackFont(Font font) {
        fallbackFont = font;
    }

    public void clearRect(int x, int y, int width, int height) {
        gfx.clearRect(x, y, width, height);
    }

    public void glyphVectorLine(GlyphVector g, float x, float y) {
        gfx.drawGlyphVector(g, x, y);
    }

    ////////////////////////////
    //     Transformation     //
    ////////////////////////////
    public void translate(double tx, double ty) {
        gfx.translate(tx, ty);
    }

    public void translate(int x, int y) {
        gfx.translate(x, y);
    }

    public void rotate(double theta) {
        gfx.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        gfx.rotate(theta, x, y);
    }

    public void scale(double sx, double sy) {
        gfx.scale(sx, sy);
    }

    public void shear(double shx, double shy) {
        gfx.shear(shx, shy);
    }

    public void transform(AffineTransform Tx) {
        gfx.transform(Tx);
    }

    public void clip(Shape s) {
        gfx.clip(s);
    }

    public void clipRect(int x, int y, int width, int height) {
        gfx.clipRect(x, y, width, height);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        gfx.copyArea(x, y, width, height, dx, dy);
    }

    /////////////////////
    //     Setters     //
    /////////////////////
    public void setTransform(AffineTransform Tx) {
        gfx.setTransform(Tx);
    }

    public void simpleClip(int x, int y, int width, int height) {
        gfx.setClip(x, y, width, height);
    }

    public void simpleClip(Shape clip) {
        gfx.setClip(clip);
    }

    /////////////////////
    //     Getters     //
    /////////////////////
    public AffineTransform getTransform() {
        return gfx.getTransform();
    }

    public Paint getPaint() {
        return gfx.getPaint();
    }

    public Composite getComposite() {
        return gfx.getComposite();
    }

    public Color getClearColor() {
        return Color.awt(gfx.getBackground());
    }

    public Stroke getStroke() {
        return gfx.getStroke();
    }

    public Color getColor() {
        return Color.awt(gfx.getColor());
    }

    public Font getFallbackFont() {
        return fallbackFont;
    }

    public Font getFont() {
        return gfx.getFont();
    }

    public FontMetrics fontMetrics(Font f) {
        return gfx.getFontMetrics(f);
    }

    public Shape getClip() {
        return gfx.getClip();
    }

    public Rectangle getClipBounds() {
        return gfx.getClipBounds();
    }

    public Rectangle getClipBounds(Rectangle r) {
        return gfx.getClipBounds(r);
    }

    public FontMetrics fontMetrics() {
        return gfx.getFontMetrics();
    }

    public FontRenderContext getFontRenderContext() {
        return gfx.getFontRenderContext();
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return gfx.getDeviceConfiguration();
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return gfx.getRenderingHint(hintKey);
    }

    public RenderingHints getRenderingHints() {
        return gfx.getRenderingHints();
    }

    ///////////////////////////
    //     Miscellaneous     //
    ///////////////////////////
    public Renderer subInstance() {
        return new Renderer(gfx.create(), observer);
    }

    public Renderer subInstance(int x, int y, int width, int height) {
        return new Renderer(gfx.create(x, y, width, height), observer);
    }

    public Renderer subInstance(com.ultreon.bubbles.render.gui.widget.Rectangle bounds) {
        return subInstance(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean hitClip(int x, int y, int width, int height) {
        return gfx.hitClip(x, y, width, height);
    }

    public void dispose() {
        gfx.dispose();
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return gfx.hit(rect, s, onStroke);
    }

    ///////////////////////
    //     To String     //
    ///////////////////////
    public String toString() {
        return gfx.toString();
    }

    public void drawGradientBox(int x, int y, int width, int height) {
        drawGradientBox(x, y, width, height, new Insets(2, 2, 2, 2));
    }

    public void drawGradientBox(int x, int y, int width, int height, Insets insets) {
        drawGradientBox(x, y, width, height, insets, 10);
    }

    public void drawGradientBox(int x, int y, int width, int height, Insets insets, int speed) {
        double shiftX = ((double) width * 2) * BubbleBlaster.getTicks() / (double)(BubbleBlaster.TPS * speed);
        GradientPaint p = new GradientPaint(x + ((float) shiftX - width), 0, Color.rgb(0x00c0ff).toAwt(), x + (float) shiftX, 0f, Color.rgb(0x00ffc0).toAwt(), true);
        Border border = new Border(insets);
        border.setPaint(p);
        border.paintBorder(this, x, y, width, height);
    }
}
