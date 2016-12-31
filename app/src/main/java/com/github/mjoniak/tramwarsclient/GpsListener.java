package com.github.mjoniak.tramwarsclient;

import com.google.android.gms.maps.model.LatLng;

class GpsListener implements IContinuation<LatLng> {

    private final ApiClient client;

    GpsListener(ApiClient client) {
        this.client = client;
    }

    @Override
    public void continueWith(final LatLng position) {
        new Thread() {
            @Override
            public void run() {
                client.authorise("abc", "Test@123", new IContinuation<AuthorisationTokenDTO>() {
                    @Override
                    public void continueWith(AuthorisationTokenDTO response) {
                        client.postPosition(position, response.getAccessToken());
                    }
                });
            }
        }.start();
    }
}
