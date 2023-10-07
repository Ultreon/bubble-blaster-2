package com.ultreon.test.bubbles;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static java.lang.System.out;

public class StringTest {
    @Test
    void splitTest1() {
        var source = "a:b:c";
        var shouldBe = new String[]{"a", "b:c"};

        var output = source.split(":", 2);

        out.println("Source: " + source);
        out.println("Should be: " + Arrays.toString(shouldBe));
        out.println("Output: " + Arrays.toString(output));

        assert Arrays.equals(output, shouldBe);
    }
}
