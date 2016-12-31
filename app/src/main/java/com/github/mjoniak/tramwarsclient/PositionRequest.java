package com.github.mjoniak.tramwarsclient;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;

class PositionRequest extends Request<PositionDTO> {
    private static final String BASE_URL = "http://192.168.2.154:5000";
    private static final String RESOURCE_PATH = "/routes/1/positions";
    private static final String AUTHORIZATION_SCHEME = "Bearer ";

    private final Gson gson = new Gson();
    private final PositionDTO dto;
    private final String accessToken;
    private final Listener<PositionDTO> listener;

    PositionRequest(PositionDTO dto, String accessToken, Listener<PositionDTO> listener, ErrorListener errorListener) {
        super(Method.POST, BASE_URL + RESOURCE_PATH, errorListener);
        this.dto = dto;
        this.accessToken = accessToken;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Authorization", AUTHORIZATION_SCHEME + " " + accessToken);
        return map;
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public byte[] getBody() {
        return gson.toJson(dto).getBytes();
    }

    @Override
    protected Response<PositionDTO> parseNetworkResponse(NetworkResponse response) {
        return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(PositionDTO response) {
        listener.onResponse(response);
    }
}
