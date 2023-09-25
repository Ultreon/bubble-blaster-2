package com.ultreon.commons.lang;

/**
 * @author XyperCode
 * @since 0.0.0
 */
@Deprecated
public class ProgressMessenger extends Progress {
    private final Messenger messenger;

    public ProgressMessenger(Messenger messenger, int progress, int max) {
        super(progress, max);
        this.messenger = messenger;
    }

    public ProgressMessenger(Messenger messenger, int max) {
        super(max);
        this.messenger = messenger;
    }

    public void send(String text) {
        messenger.send(text);
    }

    public void sendNext(String text) {
        messenger.send(text);
        increment();
    }
}
