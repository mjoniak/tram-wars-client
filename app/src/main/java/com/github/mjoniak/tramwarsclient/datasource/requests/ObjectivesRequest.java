package com.github.mjoniak.tramwarsclient.datasource.requests;

import com.android.volley.Response;
import com.github.mjoniak.tramwarsclient.datasource.dto.ObjectiveDTO;

public class ObjectivesRequest extends JsonRequest<Object, ObjectiveDTO[]> {
    private static final String RESOURCE_PATH = "/stops/%s/objectives";

    public ObjectivesRequest(
            String accessToken,
            String stopName,
            Response.Listener<ObjectiveDTO[]> listener,
            Response.ErrorListener errorListener) {

        super(Method.GET, String.format(RESOURCE_PATH, stopName), null, ObjectiveDTO[].class, accessToken, listener, errorListener);
    }
}
