package com.github.mjoniak.tramwarsclient.domain;

import android.location.Location;

import com.github.mjoniak.tramwarsclient.Const;
import com.google.android.gms.maps.model.LatLng;

public class Geography {
    private Geography() {}

    public static boolean areClose(LatLng pos1, LatLng pos2) {
        float[] dist = new float[1];
        Location.distanceBetween(pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude, dist);
        return dist[0] < Const.STOP_DISTANCE_METRES;
    }
}
