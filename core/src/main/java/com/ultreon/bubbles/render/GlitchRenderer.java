package com.ultreon.bubbles.render;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;

import java.security.SecureRandom;
import java.util.Random;

public class GlitchRenderer {
    private final char[] chars = " ~!@#$%^&*()_+{}|:\"<>?,./;'[]\\-=`1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private int matrixW;
    private int matrixH;
    private final BitmapFont font = Fonts.PRESS_START_K_14.get();
    private char[][] matrix;
    private int[][] cMatrix;
    private final int charW;
    private final int charH;

    public GlitchRenderer(BubbleBlaster game) {
        this.charW = this.font.getData().getGlyph('A').width + 4;
        this.charH = (int) this.font.getData().lineHeight;
        this.matrixW = game.getWidth() / this.charW;
        this.matrixH = game.getHeight() / this.charH;
        this.matrix = new char[this.matrixW][this.matrixH];
        this.cMatrix = new int[this.matrixW][this.matrixH];
    }

    public void resize(int width, int height) {
        this.matrixW = width / this.charW;
        this.matrixH = height / this.charH;
        this.matrix = new char[this.matrixW][this.matrixH];
        this.cMatrix = new int[this.matrixW][this.matrixH];
    }

    public void tick() {
    }

    public void addChar(char c, int x, int y, int color) {
        this.matrix[x][y] = c;
        this.cMatrix[x][y] = color;
    }

    private char randomChar() {
        return chars[new SecureRandom().nextInt(chars.length)];
    }

    public void render(Renderer renderer) {
        SecureRandom rand = new SecureRandom();
        this.addChar(randomChar(), rand.nextInt(this.matrixW), rand.nextInt(this.matrixH), new Random().nextInt(0xffffff));

        for (int x = 0; x < this.matrixW; x++) {
            for (int y = 0; y < this.matrixH; y++) {
                char matrix = this.matrix[x][y];
                int xi = x * this.charW;
                int yi = y * this.charH;

                renderer.setColor("#000");
                if (matrix != 0) {
                    renderer.fill(xi, yi, this.charW, this.charH);
                }
                renderer.setColor(Color.rgb(this.cMatrix[x][y]));

                renderer.drawText(this.font, Character.toString(matrix), xi + 2, yi);
//                renderer.text("" + matrix, xi + 2, yi + charH / 1.5f + 4);
            }
        }
    }
}
