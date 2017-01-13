package com.github.mjoniak.tramwarsclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnRequestPermissionsResultCallback {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        accessToken = getIntent().getStringExtra(Const.AUTH_TOKEN_EXTRA_KEY);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.0f, 20.0f), 15.0f));

        PermissionManager permissionManager = new PermissionManager();
        permissionManager.requestGpsPermission(this);
        permissionManager.requestInternetPermission(this);
        final ApiClient client = new ApiClient(this, new IErrorHandler() {
            @Override
            public void handle(String message) {
                Toast.makeText(MapsActivity.this, R.string.connectivity_error, Toast.LENGTH_LONG).show();
            }
        });

        final List<StopDTO> loadedStops = new ArrayList<>();
        client.getStops(accessToken, new IContinuation<StopDTO[]>() {
            @Override
            public void continueWith(StopDTO[] response) {
                for (StopDTO stop : response) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(stop.getLat(), stop.getLon()))
                            .title(stop.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }

                //TODO: possible race condition
                loadedStops.addAll(Arrays.asList(response));
            }
        });

        GpsListener listener = new GpsListener(client, accessToken);
        GpsLocator locator = new GpsLocator(getApplicationContext(), new PermissionManager());
        locator.registerListener(listener);
        locator.registerListener(new IContinuation<LatLng>() {
            @Override
            public void continueWith(LatLng response) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(response));
                for(StopDTO stop : loadedStops) {
                    LatLng stopPos = new LatLng(stop.getLat(), stop.getLon());
                    if (Geography.areClose(response, stopPos)) {
                        handleStopNearby(client, stop, response);
                    }
                }
            }
        });
    }

    private void handleStopNearby(ApiClient client, StopDTO stop, LatLng currentPosition) {
        final String serialized = formatStop(stop);
        ApplicationState appState = ApplicationState.getInstance();
        if (appState.objectiveFinished(currentPosition)) {
            Toast.makeText(MapsActivity.this, R.string.objective_finished, Toast.LENGTH_LONG).show();
            appState.resetObjective();
        }

        if (appState.hasObjective()) {
            return;
        }

        client.getObjectives(accessToken, serialized, new IContinuation<ObjectiveDTO[]>() {
            @Override
            public void continueWith(ObjectiveDTO[] response) {
                Intent intent = new Intent(MapsActivity.this, ChooseObjectiveActivity.class);
                ArrayList<String> extraObjectives = new ArrayList<>();
                for (ObjectiveDTO objectiveDTO : response)
                {
                    extraObjectives.add(formatStop(objectiveDTO.getEndStop()) + "," + objectiveDTO.getPoints());
                }

                intent.putExtra(Const.OBJECTIVES_EXTRA_KEY, extraObjectives);
                intent.putExtra(Const.AUTH_TOKEN_EXTRA_KEY, accessToken);
                intent.putExtra(Const.POSITION_EXTRA_KEY, serialized);
                startActivity(intent);
            }
        });
    }

    private String formatStop(StopDTO stop) {
        return String.format(Locale.getDefault(), "%s,%f,%f", stop.getName(), stop.getLat(), stop.getLon());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }
}