package com.ultreon.bubbles.common;

import java.io.File;
import java.io.IOException;

public interface IDownloader {
    File download() throws IOException;
}
