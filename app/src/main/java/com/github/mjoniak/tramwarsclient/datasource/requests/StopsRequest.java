package com.github.mjoniak.tramwarsclient.datasource.requests;

import com.android.volley.Response;
import com.github.mjoniak.tramwarsclient.datasource.dto.StopDTO;

public class StopsRequest extends JsonRequest<Object, StopDTO[]> {
    private static final String RESOURCE_PATH = "/stops";

    public StopsRequest(String accessToken,
                        Response.Listener<StopDTO[]> listener,
                        Response.ErrorListener errorListener) {

        super(Method.GET, RESOURCE_PATH, null, StopDTO[].class, accessToken, listener, errorListener);
    }
}
