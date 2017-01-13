package com.github.mjoniak.tramwarsclient;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;

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

    void postPosition(LatLng position, int routeId, String accessToken) {
        PositionDTO dto = new PositionDTO(position.latitude, position.longitude);
        PositionRequest request = new PositionRequest(
                dto,
                routeId,
                accessToken,
                ApiClient.<PositionDTO>getNullListener(),
                new ApiClientErrorListener());
        send(request);
    }

    void getStops(String accessToken, IContinuation<StopDTO[]> continuation) {
        StopsRequest request = new StopsRequest(
                accessToken,
                new ApiClientListener<>(continuation),
                new ApiClientErrorListener());
        send(request);
    }

    void getObjectives(String accessToken, String serializedStop, IContinuation<ObjectiveDTO[]> continuation) {
        ObjectivesRequest request = new ObjectivesRequest(
                accessToken,
                serializedStop,
                new ApiClientListener<>(continuation),
                new ApiClientErrorListener());
        send(request);
    }

    void postRoute(String accessToken,
                   String stopName,
                   float lat,
                   float lng,
                   String targetStopName,
                   float targetLat,
                   float targetLng,
                   IContinuation<RouteDTO> continuation) {
        StopDTO startDto = new StopDTO(stopName, lat, lng);
        StopDTO targetDto = new StopDTO(targetStopName, targetLat, targetLng);
        PostRouteRequest request = new PostRouteRequest(
                accessToken,
                new StopDTO[] { startDto, targetDto },
                new ApiClientListener<>(continuation),
                new ApiClientErrorListener());
        send(request);
    }

    private void send(Request req) {
        queue.add(req);
    }

    private static <T> ApiClientListener<T> getNullListener() {
        return new ApiClientListener<>(new IContinuation<T>() {
            @Override
            public void continueWith(T response) {
            }
        });
    }

    private class ApiClientErrorListener implements ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            errorHandler.handle(error.getMessage());
        }
    }

    private static class ApiClientListener<T> implements Listener<T> {
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
