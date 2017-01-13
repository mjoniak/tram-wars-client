package com.github.mjoniak.tramwarsclient;

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


class AuthorisationRequest extends Request<AuthorisationTokenDTO> {
    private static final String RESOURCE_PATH = "/token";

    private final Gson gson = new Gson();
    private final AuthorisationDTO dto;
    private final Listener<AuthorisationTokenDTO> listener;

    AuthorisationRequest(AuthorisationDTO dto, Listener<AuthorisationTokenDTO> listener, ErrorListener errorListener) {
        super(Method.POST, Const.BASE_URL + RESOURCE_PATH, errorListener);
        this.dto = dto;
        this.listener = listener;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String, String> map = new HashMap<>();
        map.put("grant_type", dto.getGrantType());
        map.put("username", dto.getUsername());
        map.put("password", dto.getPassword());
        return map;
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded";
    }

    @Override
    protected Response<AuthorisationTokenDTO> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            AuthorisationTokenDTO token = gson.fromJson(json, AuthorisationTokenDTO.class);
            return Response.success(token, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new VolleyError(e));
        }
    }

    @Override
    protected void deliverResponse(AuthorisationTokenDTO response) {
        listener.onResponse(response);
    }
}
