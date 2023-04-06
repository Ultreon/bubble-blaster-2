package com.ultreon.bubbles.util.helpers;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.settings.GameSettings;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Immutable class to get the Image representation create a svg resource.
 */
@SuppressWarnings({"unused", "DuplicatedCode"})
public final class SvgHelper {

    /**
     * Root node create svg document
     */
    private final GraphicsNode rootSvgNode;
    /**
     * Loaded SVG document
     */
    private final SVGDocument svgDocument;

    /**
     * Load the svg resource from a URL &amp; InputStream into a document.
     *
     * @param url location create svg resource.
     * @throws java.io.IOException when svg resource cannot be read.
     */
    public SvgHelper(URL url, InputStream stream) throws IOException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        svgDocument =
                (SVGDocument) factory.createDocument(url.toString(), stream);
        rootSvgNode = getRootNode(svgDocument);
    }

    /**
     * Load the svg resource from a URL into a document.
     *
     * @param url location create svg resource.
     * @throws java.io.IOException when svg resource cannot be read.
     */
    public SvgHelper(URL url) throws IOException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        svgDocument =
                (SVGDocument) factory.createDocument(url.toString());
        rootSvgNode = getRootNode(svgDocument);
    }

    /**
     * Load the svg from a document.
     *
     * @param document svg resource
     */
    public SvgHelper(SVGDocument document) {
        svgDocument = document;
        rootSvgNode = getRootNode(svgDocument);
    }

    /**
     * Get svg root from the given document.
     *
     * @param document svg resource
     */
    private static GraphicsNode getRootNode(SVGDocument document) {
        // Build the tree and get the document dimensions
        UserAgentAdapter userAgentAdapter = new UserAgentAdapter();
        BridgeContext bridgeContext = new BridgeContext(userAgentAdapter);
        GVTBuilder builder = new GVTBuilder();

        return builder.build(bridgeContext, document);
    }

    /**
     * Get the svg root node create the document.
     *
     * @return svg root node.
     */
    public GraphicsNode getRootSvgNode() {
        return rootSvgNode;
    }

    /**
     * Get the svg document.
     *
     * @return the svg document.
     */
    public SVGDocument getSvgDocument() {
        return svgDocument;
    }

    /**
     * Renders and returns the svg based image.
     *
     * @param width  desired width, if it is less than or equal to 0 aspect
     *               ratio is preserved and the size is determined by height.
     * @param height desired height, if it is less than or equal to 0 aspect
     *               ratio is preserved and the size is determined by width.
     * @return image create the rendered svg.'
     * TODO: modify to also give a image that preserves aspects but matches
     * width or height individually.
     */
    public Image getImage(int width, int height) {
        /* Adjusts the scale create the transformation below, if either width or
         * height is less than or equal to 0 the aspect ratio is preserved.
         */
        Element elt = svgDocument.getRootElement();
        Rectangle2D bounds = rootSvgNode.getPrimitiveBounds();
        double scaleX, scaleY;
        if (width <= 0) {
            scaleX = scaleY = height / bounds.getHeight();
            width = (int) (scaleX * bounds.getWidth());
        } else if (height <= 0) {
            scaleX = scaleY = width / bounds.getWidth();
            height = (int) (scaleY * bounds.getHeight());
        } else {
            scaleX = width / bounds.getWidth();
            scaleY = height / bounds.getHeight();
        }

        // Paint svg into image buffer
        BufferedImage bufferedImage = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();

        // For a smooth graphic with no jagged edges or rasterized look.

        if (BubbleBlaster.getInstance().getRenderSettings().isAntialiasingEnabled() && GameSettings.instance().getGraphicsSettings().isAntialiasEnabled())
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING, RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING);

        // Scale image to desired size
        AffineTransform usr2dev =
                new AffineTransform(scaleX, 0.0, 0.0, scaleY, 0.0, 0.0);
        g2d.transform(usr2dev);
        rootSvgNode.paint(g2d);

        // Cleanup and return image
        g2d.dispose();
        return bufferedImage;
    }

    /**
     * Renders and returns the svg based image.
     *
     * @param width  desired width, if it is less than or equal to 0 aspect ratio is preserved and the size is determined by height.
     * @param height desired height, if it is less than or equal to 0 aspect ratio is preserved and the size is determined by width.
     * @return image create the rendered svg.'
     * TODO: modify to also give a image that preserves aspects but matches
     * width or height individually.
     */
    public Image getColoredImage(int width, int height, com.ultreon.bubbles.render.Color fill) {
        return getColoredImage(width, height, fill.toAwt());
    }

    /**
     * Renders and returns the svg based image.
     *
     * @param width  desired width, if it is less than or equal to 0 aspect ratio is preserved and the size is determined by height.
     * @param height desired height, if it is less than or equal to 0 aspect ratio is preserved and the size is determined by width.
     * @param fill   desired fill color.
     * @return image create the rendered svg.'
     * TODO: modify to also give a image that preserves aspects but matches
     * width or height individually.
     */
    public Image getColoredImage(int width, int height, Color fill) {
        return getColoredImage(width, height, fill, null);
    }

    /**
     * Renders and returns the svg based image.
     *
     * @param width  desired width, if it is less than or equal to 0 aspect ratio is preserved and the size is determined by height.
     * @param height desired height, if it is less than or equal to 0 aspect ratio is preserved and the size is determined by width.
     * @return image create the rendered svg.'
     * TODO: modify to also give a image that preserves aspects but matches
     * width or height individually.
     */
    public Image getColoredImage(int width, int height, com.ultreon.bubbles.render.Color fill, com.ultreon.bubbles.render.Color outline) {
        return getColoredImage(width, height, fill.toAwt(), outline.toAwt());
    }

    /**
     * Renders and returns the svg based image.
     *
     * @param width   desired width, if it is less than or equal to 0 aspect ratio is preserved and the size is determined by height.
     * @param height  desired height, if it is less than or equal to 0 aspect ratio is preserved and the size is determined by width.
     * @param fill    desired fill color.
     * @param outline desired outline color.
     * @return image create the rendered svg.'
     * TODO: modify to also give a image that preserves aspects but matches
     * width or height individually.
     */
    public Image getColoredImage(int width, int height, Color fill, Color outline) {
        /* Adjusts the scale create the transformation below, if either width or
         * height is less than or equal to 0 the aspect ratio is preserved.
         */
        Element elt = svgDocument.getRootElement();
        Rectangle2D bounds = rootSvgNode.getPrimitiveBounds();
        double scaleX, scaleY;
        if (width <= 0) {
            scaleX = scaleY = height / bounds.getHeight();
            width = (int) (scaleX * bounds.getWidth());
        } else if (height <= 0) {
            scaleX = scaleY = width / bounds.getWidth();
            height = (int) (scaleY * bounds.getHeight());
        } else {
            scaleX = width / bounds.getWidth();
            scaleY = height / bounds.getHeight();
        }

        // Paint svg into image buffer
        BufferedImage bufferedImage = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();

        // For a smooth graphic with no jagged edges or rasterized look.
        if (BubbleBlaster.getInstance().getRenderSettings().isAntialiasingEnabled() && GameSettings.instance().getGraphicsSettings().isAntialiasEnabled())
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING, RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING);

        // Scale image to desired size
        AffineTransform usr2dev =
                new AffineTransform(scaleX, 0.0, 0.0, scaleY, 0.0, 0.0);
        g2d.transform(usr2dev);
        if (fill != null) {
            g2d.setColor(fill);
            g2d.fill(rootSvgNode.getOutline());
        }
        if (outline != null) {
            g2d.setColor(outline);
            g2d.fill(rootSvgNode.getOutline());
        }

        // Cleanup and return image
        g2d.dispose();
        return bufferedImage;
    }
}