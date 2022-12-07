package com.ultreon.bubbles.render;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.Ticker;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;

public class GlitchRenderer {
    private final BubbleBlaster game;
    private final Ticker ticker = new Ticker();
    @SuppressWarnings("SpellCheckingInspection")
    private final char[] chars = " ~!@#$%^&*()_+{}|:\"<>?,./;'[]\\-=`1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final int matrixW;
    private final int matrixH;
    private final Font font = new Font("Press Start K", Font.PLAIN, 18);
    private final char[][] matrix;
    private final int[][] cMatrix;
    private final int charW;
    private final int charH;

    public GlitchRenderer(BubbleBlaster game) {
        this.game = game;
        charW = game.getFontMetrics(font).charWidth('A') + 4;
        charH = game.getFontMetrics(font).getHeight() + 4;
        matrixW = game.getWidth() / charW;
        matrixH = game.getHeight() / charH;
        matrix = new char[matrixW][matrixH];
        cMatrix = new int[matrixW][matrixH];
    }

    public void tick() {
    }

    public void addChar(char c, int x, int y, int color) {
        matrix[x][y] = c;
        cMatrix[x][y] = color;
    }

    private char randomChar() {
        return chars[new SecureRandom().nextInt(chars.length)];
    }

    public void render(Renderer renderer) {
        SecureRandom rand = new SecureRandom();
        this.addChar(randomChar(), rand.nextInt(matrixW), rand.nextInt(matrixH), new Random().nextInt(0xffffff));

        renderer.hint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        for (int x = 0; x < matrixW; x++) {
            for (int y = 0; y < matrixH; y++) {
                char matrix = this.matrix[x][y];
                int xi = x * charW;
                int yi = y * charH;

                renderer.font(font);
                renderer.color("#000");
                if (matrix != 0) {
                    renderer.rect(xi, yi, charW, charH);
                }
                renderer.color(new Color(cMatrix[x][y]));
                renderer.text("" + matrix, xi + 2, yi + charH / 1.5f + 4);
            }
        }
    }
}
