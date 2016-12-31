package com.github.mjoniak.tramwarsclient;

interface IContinuation<T> {
    void continueWith(T response);
}
