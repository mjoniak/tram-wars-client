package com.github.mjoniak.tramwarsclient;

import com.android.volley.Response;

class PostRouteRequest extends JsonRequest<StopDTO[], RouteDTO> {
    private static final String RESOURCE_PATH = "/routes";

    PostRouteRequest(String accessToken, StopDTO[] dtos, Response.Listener<RouteDTO> listener, Response.ErrorListener errorListener) {
        super(Method.POST, RESOURCE_PATH, dtos, RouteDTO.class, accessToken, listener, errorListener);
    }
}
