package com.github.mjoniak.tramwarsclient.datasource.requests;

import com.android.volley.Response;
import com.github.mjoniak.tramwarsclient.domain.UserProfile;

public class PutProfileRequest extends JsonRequest<UserProfile, Object> {
    private static final String RESOURCE_PATH = "/users/%s?password=%s";

    public PutProfileRequest(String accessToken, String username, String currentPassword, UserProfile profile, Response.Listener<Object> listener, Response.ErrorListener errorListener) {
        super(Method.PUT, String.format(RESOURCE_PATH, username, currentPassword), profile, Object.class, accessToken, listener, errorListener);
    }
}
