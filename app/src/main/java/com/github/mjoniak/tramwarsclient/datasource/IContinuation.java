package com.github.mjoniak.tramwarsclient.datasource;

public interface IContinuation<T> {
    void continueWith(T response);
}
