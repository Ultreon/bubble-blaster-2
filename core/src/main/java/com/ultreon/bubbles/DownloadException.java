package com.ultreon.bubbles;

public class DownloadException extends RuntimeException {
    public DownloadException() {
        super();
    }

    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadException(Throwable cause) {
        super(cause);
    }
}
