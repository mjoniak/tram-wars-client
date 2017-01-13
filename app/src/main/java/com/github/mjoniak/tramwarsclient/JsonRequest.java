package com.github.mjoniak.tramwarsclient;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;

class JsonRequest<RQ, RS> extends Request<RS> {
    private static final String AUTHORIZATION_SCHEME = "Bearer";

    private final Gson gson = new Gson();
    private final RQ dto;
    private final String accessToken;
    private final Listener<RS> listener;
    private final Class<RS> clazz;

    JsonRequest(int method,
                String resourcePath,
                RQ dto,
                Class<RS> clazz,
                String accessToken,
                Listener<RS> listener,
                ErrorListener errorListener) {

        super(method, Const.BASE_URL + resourcePath, errorListener);
        this.dto = dto;
        this.clazz = clazz;
        this.accessToken = accessToken;
        this.listener = listener;
    }

    JsonRequest(int method,
                String resourcePath,
                RQ dto,
                String accessToken,
                Listener<RS> listener,
                ErrorListener errorListener) {

        this(method, resourcePath, dto, null, accessToken, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Authorization", AUTHORIZATION_SCHEME + " " + accessToken);
        return map;
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (dto == null) {
            return super.getBody();
        }

        return gson.toJson(dto).getBytes();
    }

    @Override
    protected Response<RS> parseNetworkResponse(NetworkResponse response) {
        RS parsed = null;
        if (clazz != null) {
            try {
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                parsed = gson.fromJson(json, clazz);
            } catch (UnsupportedEncodingException e) {
                return Response.error(new VolleyError(e));
            }
        }

        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(RS response) {
        listener.onResponse(response);
    }
}
