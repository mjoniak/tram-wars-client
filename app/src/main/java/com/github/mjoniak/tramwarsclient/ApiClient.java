package com.github.mjoniak.tramwarsclient;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import static com.android.volley.Response.*;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

class ApiClient {
    private static final String GRANT_TYPE = "password";

    private final RequestQueue queue;
    private final IErrorHandler errorHandler;

    ApiClient(Context context, IErrorHandler errorHandler) {
        this.queue = Volley.newRequestQueue(context.getApplicationContext());
        this.errorHandler = errorHandler;
    }

    void authorise(String username, String password, IContinuation<AuthorisationTokenDTO> continuation) {
        AuthorisationDTO dto = new AuthorisationDTO(GRANT_TYPE, username, password);
        AuthorisationRequest request = new AuthorisationRequest(
                dto,
                new ApiClientListener<>(continuation),
                new ApiClientErrorListener());
        send(request);
    }

    void postPosition(LatLng position, String accessToken) {
        PositionDTO dto = new PositionDTO(position.latitude, position.longitude);
        PositionRequest request = new PositionRequest(
                dto,
                accessToken,
                new ApiClientListener<>(new IContinuation<PositionDTO>() {
                    @Override
                    public void continueWith(PositionDTO response) {
                    }
                }),
                new ApiClientErrorListener());
        send(request);
    }

    private void send(Request req) {
        queue.add(req);
    }

    private class ApiClientErrorListener implements ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            errorHandler.handle(error.getMessage());
        }
    }

    private class ApiClientListener<T> implements Listener<T> {
        private final IContinuation<T> continuation;

        ApiClientListener(IContinuation<T> continuation) {
            this.continuation = continuation;
        }

        @Override
        public void onResponse(T response) {
            continuation.continueWith(response);
        }
    }
}
