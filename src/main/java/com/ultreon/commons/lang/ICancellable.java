package com.ultreon.commons.lang;

public interface ICancellable {
    void cancel();

    boolean isCancelled();
}
