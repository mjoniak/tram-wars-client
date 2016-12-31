package com.github.mjoniak.tramwarsclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnRequestPermissionsResultCallback {

    private final LatLng centerLatLng = new LatLng(50, 20);
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.github.mjoniak.tramwarsclient.R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.github.mjoniak.tramwarsclient.R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(centerLatLng));
        PermissionManager permissionManager = new PermissionManager();
        permissionManager.requestGpsPermission(this);
        permissionManager.requestInternetPermission(this);
        ApiClient client = new ApiClient(this, new IErrorHandler() {
            @Override
            public void handle(String message) {
                Toast.makeText(MapsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
        GpsListener listener = new GpsListener(client);
        GpsLocator locator = new GpsLocator(getApplicationContext(), new PermissionManager());
        locator.registerListener(listener);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        updateCurrentPositionMarker();
    }

    private void updateCurrentPositionMarker() {
//        GpsLocator locator = new GpsLocator(this, new PermissionManager());
//        try {
//            LatLng pos = locator.getCurrentPosition();
//            if (googleMap != null) {
//                googleMap.addMarker(new MarkerOptions().position(pos));
//            }
//        } catch (CantGetLocationException e) {
//            Toast.makeText(this, "Please enable GPS!", Toast.LENGTH_LONG).show();
//        }
    }
}