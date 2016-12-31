package com.github.mjoniak.tramwarsclient;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

class PermissionManager {

    boolean checkGpsPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
    }

    void requestGpsPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] {
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION },
                0);
    }

    void requestInternetPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.INTERNET }, 0);
    }
}
