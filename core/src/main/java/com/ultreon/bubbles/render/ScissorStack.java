package com.ultreon.bubbles.render;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.mixins.ScissorStackMixin;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScissorStack {
	public static Deque<Rectangle> scissors = new ArrayDeque<>();

	@CanIgnoreReturnValue
	public static boolean pushScissors(Rectangle scissor) {
		return com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.pushScissors(scissor);
    }

	@CanIgnoreReturnValue
	public static Rectangle popScissors() {
		return com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.popScissors();
	}

	private static void fix(Rectangle rect) {
		rect.x = Math.round(rect.x);
		rect.y = Math.round(rect.y);
		rect.width = Math.round(rect.width);
		rect.height = Math.round(rect.height);
		if (rect.width < 0) {
			rect.width = -rect.width;
			rect.x -= rect.width;
		}
		if (rect.height < 0) {
			rect.height = -rect.height;
			rect.y -= rect.height;
		}
	}

	public static Rectangle peekScissors() {
		return com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.peekScissors();
	}

	public static boolean isEmpty() {
		return ScissorStackMixin.getScissors().isEmpty();
	}
}
