package com.github.mjoniak.tramwarsclient.domain;

import com.google.android.gms.maps.model.LatLng;

public class Objective {
    private final LatLng target;
    private final int routeId;

    public Objective(LatLng target, int routeId) {
        this.target = target;
        this.routeId = routeId;
    }

    public int getRouteId() {
        return routeId;
    }

    public boolean finished(LatLng currentPosition) {
        return Geography.areClose(currentPosition, target);
    }
}
