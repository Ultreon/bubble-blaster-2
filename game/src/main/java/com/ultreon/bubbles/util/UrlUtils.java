package com.ultreon.bubbles.util;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {

    public static URL safeUrl(String spec) {
        try {
            return new URL(spec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
