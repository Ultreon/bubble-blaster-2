package com.ultreon.commons.lang;

@Deprecated
public interface ICancellable {
    void cancel();

    boolean isCancelled();
}
