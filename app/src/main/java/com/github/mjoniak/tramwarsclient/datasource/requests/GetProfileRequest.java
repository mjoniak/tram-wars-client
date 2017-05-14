package com.github.mjoniak.tramwarsclient.datasource.requests;

import com.android.volley.Response;
import com.github.mjoniak.tramwarsclient.domain.UserProfile;

public class GetProfileRequest extends JsonRequest<Object, UserProfile> {
    private static final String RESOURCE_PATH = "/users/%s";

    public GetProfileRequest(String accessToken,
                             String userName,
                             Response.Listener<UserProfile> listener,
                             Response.ErrorListener errorListener) {

        super(Method.GET, String.format(RESOURCE_PATH, userName), null, UserProfile.class, accessToken, listener, errorListener);
    }
}
