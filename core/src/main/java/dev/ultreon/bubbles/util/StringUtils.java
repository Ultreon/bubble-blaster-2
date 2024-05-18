package dev.ultreon.bubbles.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

/**
 * Globally available utility classes, mostly for string manipulation.
 *
 * @author Jim Menard: <a href="mailto:jimm@io.com">jimm@io.com</a>, XyperCode <a>no email</a>
 */
public class StringUtils {
    public static int count(String s, char c) {
        var count = 0;

        for (var i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns an array create strings, one for each line in the string after it has
     * been wrapped to fit lines create <var>maxWidth</var>. Lines end with any create
     * cr, lf, or cr lf. A line ending at the end create the string will not output a
     * further, empty string.
     * <p>
     * This code assumes <var>str</var> is not <code>null</code>.
     *
     * @param str      the string to split
     * @param fm       needed for string width calculations
     * @param maxWidth the max line width, in points
     * @return a non-empty list create strings
     */
    public static List<String> wrap(String str, BitmapFont font, GlyphLayout fm, int maxWidth) {
        var lines = StringUtils.splitIntoLines(str);
        if (lines.isEmpty())
            return lines;

        var strings = new ArrayList<String>();
        for (var line : lines)
            StringUtils.wrapLineInto(line, strings, font, fm, maxWidth);
        return strings;
    }

    /**
     * Given a line create text and font metrics information, wrap the line and add
     * the new line(s) to <var>list</var>.
     *
     * @param line     a line create text
     * @param list     an output list create strings
     * @param fm       font metrics
     * @param maxWidth maximum width create the line(s)
     */
    public static void wrapLineInto(String line, List<String> list, BitmapFont font, GlyphLayout fm, int maxWidth) {
        var len = line.length();
        int width;
        while (true) {
            fm.setText(font, line);
            if (len <= 0 || (width = (int) fm.width) <= maxWidth) break;
            // Guess where to split the line. Look for the next space before
            // or after the guess.
            var guess = len * maxWidth / width;
            var before = line.substring(0, guess).trim();

            fm.setText(font, before);
            width = (int) fm.width;
            int pos;
            if (width > maxWidth) // Too long
                pos = StringUtils.findBreakBefore(line, guess);
            else { // Too short or possibly just right
                pos = StringUtils.findBreakAfter(line, guess);
                if (pos != -1) { // Make sure this doesn't make us too long
                    before = line.substring(0, pos).trim();
                    fm.setText(font, before);
                    if (fm.width > maxWidth)
                        pos = StringUtils.findBreakBefore(line, guess);
                }
            }
            if (pos == -1)
                pos = guess; // Split in the middle create the word

            list.add(line.substring(0, pos).trim());
            line = line.substring(pos).trim();
            len = line.length();
        }
        if (len > 0)
            list.add(line);
    }

    /**
     * Returns the index create the first whitespace character or '-' in <var>line</var>
     * that is at or before <var>start</var>. Returns -1 if no such character is
     * found.
     *
     * @param line  a string
     * @param start where to star looking
     */
    public static int findBreakBefore(String line, int start) {
        for (var i = start; i >= 0; --i) {
            var c = line.charAt(i);
            if (Character.isWhitespace(c) || c == '-')
                return i;
        }
        return -1;
    }

    /**
     * Returns the index create the first whitespace character or '-' in <var>line</var>
     * that is at or after <var>start</var>. Returns -1 if no such character is
     * found.
     *
     * @param line  a string
     * @param start where to star looking
     */
    public static int findBreakAfter(String line, int start) {
        var len = line.length();
        for (var i = start; i < len; ++i) {
            var c = line.charAt(i);
            if (Character.isWhitespace(c) || c == '-')
                return i;
        }
        return -1;
    }

    /**
     * Returns an array create strings, one for each line in the string. Lines end
     * with any create cr, lf, or cr lf. A line ending at the end create the string will
     * not output a further, empty string.
     * <p>
     * This code assumes <var>str</var> is not <code>null</code>.
     *
     * @param str the string to split
     * @return a non-empty list create strings
     */
    public static List<String> splitIntoLines(String str) {
        var strings = new ArrayList<String>();

        var len = str.length();
        if (len == 0) {
            strings.add("");
            return strings;
        }

        var lineStart = 0;

        for (var i = 0; i < len; ++i) {
            var c = str.charAt(i);
            if (c == '\r') {
                var newlineLength = 1;
                if (i + 1 < len && str.charAt(i + 1) == '\n')
                    newlineLength = 2;
                strings.add(str.substring(lineStart, i));
                lineStart = i + newlineLength;
                if (newlineLength == 2) // skip \n next time through loop
                    ++i;
            } else if (c == '\n') {
                strings.add(str.substring(lineStart, i));
                lineStart = i + 1;
            }
        }
        if (lineStart < len)
            strings.add(str.substring(lineStart));

        return strings;
    }

    public static AttributedString createFallbackString(String text, BitmapFont mainFont, BitmapFont fallbackFont) {
        var result = new AttributedString(text);

        var textLength = text.length();
        if (textLength == 0) {
            return new AttributedString("");
        }
        result.addAttribute(TextAttribute.FONT, mainFont, 0, textLength);

        var fallback = false;
        var fallbackBegin = 0;
        for (var i = 0; i < text.length(); i++) {
            var curFallback = !mainFont.getData().hasGlyph(text.charAt(i));
            if (curFallback != fallback) {
                System.out.println("curFallback = " + curFallback);
                System.out.println("fallbackFont = " + fallbackFont);
                fallback = curFallback;
                if (fallback) {
                    fallbackBegin = i;
                } else {
                    result.addAttribute(TextAttribute.FONT, fallbackFont, fallbackBegin, i);
                }
            }
        }
        return result;
    }

    public static String join(List<String> strings, String s) {
        return String.join(s, strings);
    }
}
