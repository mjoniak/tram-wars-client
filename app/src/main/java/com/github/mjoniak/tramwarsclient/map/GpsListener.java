package com.github.mjoniak.tramwarsclient.map;

import com.github.mjoniak.tramwarsclient.ApplicationState;
import com.github.mjoniak.tramwarsclient.datasource.IContinuation;
import com.github.mjoniak.tramwarsclient.datasource.ApiClient;
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
