package com.github.mjoniak.tramwarsclient;

import com.google.android.gms.maps.model.LatLng;

class GpsListener implements IContinuation<LatLng> {

    private final ApiClient client;
    private final String accessToken;

    GpsListener(ApiClient client, String accessToken) {
        this.client = client;
        this.accessToken = accessToken;
    }

    @Override
    public void continueWith(final LatLng position) {
        new Thread() {
            @Override
            public void run() {
                ApplicationState state = ApplicationState.getInstance();
                if (state.hasObjective()) {
                    client.postPosition(position, state.getRouteId(), accessToken);
                }
            }
        }.start();
    }
}
