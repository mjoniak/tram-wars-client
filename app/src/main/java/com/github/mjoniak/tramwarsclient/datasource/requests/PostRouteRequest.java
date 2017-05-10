package com.github.mjoniak.tramwarsclient.datasource.requests;

import com.android.volley.Response;
import com.github.mjoniak.tramwarsclient.datasource.dto.RouteDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.StopDTO;

public class PostRouteRequest extends JsonRequest<StopDTO[], RouteDTO> {
    private static final String RESOURCE_PATH = "/routes";

    public PostRouteRequest(String accessToken, StopDTO[] dtos, Response.Listener<RouteDTO> listener, Response.ErrorListener errorListener) {
        super(Method.POST, RESOURCE_PATH, dtos, RouteDTO.class, accessToken, listener, errorListener);
    }
}
