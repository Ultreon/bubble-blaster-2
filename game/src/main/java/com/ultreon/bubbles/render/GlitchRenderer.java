package com.ultreon.bubbles.render;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.font.Font;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;

public class GlitchRenderer {
    private final char[] chars = " ~!@#$%^&*()_+{}|:\"<>?,./;'[]\\-=`1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final int matrixW;
    private final int matrixH;
    private final Font font = Fonts.PRESS_START_K.get();
    private final char[][] matrix;
    private final int[][] cMatrix;
    private final int charW;
    private final int charH;

    public GlitchRenderer(BubbleBlaster game) {
        charW = font.width(18, 'A') + 4;
        charH = font.height(18) + 4;
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

                renderer.color("#000");
                if (matrix != 0) {
                    renderer.rect(xi, yi, charW, charH);
                }
                renderer.color(Color.rgb(cMatrix[x][y]));
                font.drawString(renderer, Character.toString(matrix), 18, xi + 2, yi + charH / 1.5f + 4);
//                renderer.text("" + matrix, xi + 2, yi + charH / 1.5f + 4);
            }
        }
    }
}
