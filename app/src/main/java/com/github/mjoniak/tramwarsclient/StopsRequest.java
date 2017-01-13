package com.github.mjoniak.tramwarsclient;

import com.android.volley.Response;

class StopsRequest extends JsonRequest<Object, StopDTO[]> {
    private static final String RESOURCE_PATH = "/stops";

    StopsRequest(String accessToken,
                 Response.Listener<StopDTO[]> listener,
                 Response.ErrorListener errorListener) {

        super(Method.GET, RESOURCE_PATH, null, StopDTO[].class, accessToken, listener, errorListener);
    }
}
