package com.ultreon.test.bubbles;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static java.lang.System.out;

public class StringTest {
    @Test
    void splitTest1() {
        String source = "a:b:c";
        String[] shouldBe = {"a", "b:c"};

        String[] output = source.split(":", 2);

        out.println("Source: " + source);
        out.println("Should be: " + Arrays.toString(shouldBe));
        out.println("Output: " + Arrays.toString(output));

        assert Arrays.equals(output, shouldBe);
    }
}
