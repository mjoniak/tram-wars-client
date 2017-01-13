package com.github.mjoniak.tramwarsclient;

import com.google.android.gms.maps.model.LatLng;

class Objective {
    private final LatLng target;
    private final int routeId;

    Objective(LatLng target, int routeId) {
        this.target = target;
        this.routeId = routeId;
    }

    int getRouteId() {
        return routeId;
    }

    boolean finished(LatLng currentPosition) {
        return Geography.areClose(currentPosition, target);
    }
}
