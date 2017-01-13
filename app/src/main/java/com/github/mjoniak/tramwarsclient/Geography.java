package com.github.mjoniak.tramwarsclient;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

class Geography {
    private Geography() {}

    static boolean areClose(LatLng pos1, LatLng pos2) {
        float[] dist = new float[1];
        Location.distanceBetween(pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude, dist);
        return dist[0] < Const.STOP_DISTANCE_METRES;
    }
}
