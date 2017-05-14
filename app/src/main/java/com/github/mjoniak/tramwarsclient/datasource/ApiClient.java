package com.github.mjoniak.tramwarsclient.datasource;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.mjoniak.tramwarsclient.datasource.dto.AuthorisationDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.AuthorisationTokenDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.ObjectiveDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.PositionDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.RouteDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.StopDTO;
import com.github.mjoniak.tramwarsclient.datasource.requests.AuthorisationRequest;
import com.github.mjoniak.tramwarsclient.datasource.requests.GetProfileRequest;
import com.github.mjoniak.tramwarsclient.datasource.requests.ObjectivesRequest;
import com.github.mjoniak.tramwarsclient.datasource.requests.PositionRequest;
import com.github.mjoniak.tramwarsclient.datasource.requests.PostRouteRequest;
import com.github.mjoniak.tramwarsclient.datasource.requests.PutProfileRequest;
import com.github.mjoniak.tramwarsclient.datasource.requests.StopsRequest;
import com.github.mjoniak.tramwarsclient.domain.UserProfile;
import com.google.android.gms.maps.model.LatLng;

import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;

public class ApiClient {
    private static final String GRANT_TYPE = "password";

    private final RequestQueue queue;
    private final IErrorHandler errorHandler;

    public ApiClient(Context context, IErrorHandler errorHandler) {
        this.queue = Volley.newRequestQueue(context.getApplicationContext());
        this.errorHandler = errorHandler;
    }

    public void authorise(String username, String password, IContinuation<AuthorisationTokenDTO> continuation) {
        AuthorisationDTO dto = new AuthorisationDTO(GRANT_TYPE, username, password);
        AuthorisationRequest request = new AuthorisationRequest(
                dto,
                new ApiClientListener<>(continuation),
                new ApiClientErrorListener());
        send(request);
    }

    public void postPosition(LatLng position, int routeId, String accessToken) {
        PositionDTO dto = new PositionDTO(position.latitude, position.longitude);
        PositionRequest request = new PositionRequest(
                dto,
                routeId,
                accessToken,
                ApiClient.<PositionDTO>getNullListener(),
                new ApiClientErrorListener());
        send(request);
    }

    public void getStops(String accessToken, IContinuation<StopDTO[]> continuation) {
        StopsRequest request = new StopsRequest(
                accessToken,
                new ApiClientListener<>(continuation),
                new ApiClientErrorListener());
        send(request);
    }

    public void getObjectives(String accessToken, String serializedStop, IContinuation<ObjectiveDTO[]> continuation) {
        ObjectivesRequest request = new ObjectivesRequest(
                accessToken,
                serializedStop,
                new ApiClientListener<>(continuation),
                new ApiClientErrorListener());
        send(request);
    }

    public void postRoute(String accessToken,
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


    public void getProfile(String accessToken, String userName, IContinuation<UserProfile> continuation) {
        GetProfileRequest request = new GetProfileRequest(
                accessToken, userName,
                new ApiClientListener<>(continuation), new ApiClientErrorListener());
        send(request);
    }


    public void putProfile(String accessToken, String userName, String currentPassword, UserProfile userProfile, IContinuation<Object> continuation) {
        PutProfileRequest request = new PutProfileRequest(
                accessToken, userName, currentPassword, userProfile,
                new ApiClientListener<>(continuation), new ApiClientErrorListener());
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
