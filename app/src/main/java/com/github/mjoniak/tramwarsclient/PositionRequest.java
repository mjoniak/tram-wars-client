package com.github.mjoniak.tramwarsclient;

import java.util.Locale;

import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;

class PositionRequest extends JsonRequest<PositionDTO, PositionDTO> {
    private static final String RESOURCE_PATH = "/routes/%d/positions";

    PositionRequest(PositionDTO dto,
                    int routeId,
                    String accessToken,
                    Listener<PositionDTO> listener,
                    ErrorListener errorListener) {

        super(Method.POST, String.format(Locale.getDefault(), RESOURCE_PATH, routeId), dto, accessToken, listener, errorListener);
    }
}
