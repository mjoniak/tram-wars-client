package com.github.mjoniak.tramwarsclient;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

class GpsLocator {
    private static final long MIN_TIME = 1000L;
    private static final float MIN_DISTANCE = 3.0f;

    private final Context context;
    private final PermissionManager permissionManager;

    GpsLocator(Context context, PermissionManager permissionManager) {
        this.context = context;
        this.permissionManager = permissionManager;
    }

    void registerListener(final IContinuation<LatLng> listener) {
        listen(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                listener.continueWith(getLatLng(location));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    @NonNull
    private LatLng getLatLng(Location loc) {
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    @NonNull
    private LocationManager listen(LocationListener listener) throws SecurityException {
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if (!permissionManager.checkGpsPermissions(context)) {
            throw new SecurityException("GPS not allowed!");
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, listener);
        return locationManager;
    }
}
