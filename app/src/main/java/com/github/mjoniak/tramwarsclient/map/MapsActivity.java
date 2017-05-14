package com.github.mjoniak.tramwarsclient.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mjoniak.tramwarsclient.ApplicationState;
import com.github.mjoniak.tramwarsclient.Const;
import com.github.mjoniak.tramwarsclient.PermissionManager;
import com.github.mjoniak.tramwarsclient.R;
import com.github.mjoniak.tramwarsclient.chooseobjective.ChooseObjectiveActivity;
import com.github.mjoniak.tramwarsclient.datasource.ApiClient;
import com.github.mjoniak.tramwarsclient.datasource.IContinuation;
import com.github.mjoniak.tramwarsclient.datasource.IErrorHandler;
import com.github.mjoniak.tramwarsclient.datasource.dto.ObjectiveDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.StopDTO;
import com.github.mjoniak.tramwarsclient.domain.Geography;
import com.github.mjoniak.tramwarsclient.profile.ProfileActivity;
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

public class MapsActivity
        extends AppCompatActivity
        implements OnMapReadyCallback, OnRequestPermissionsResultCallback, MapContract.View {

    private String accessToken;
    private MapContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new MapPresenter(this);

        setContentView(R.layout.activity_maps);
        accessToken = ApplicationState.getInstance().getAccessToken();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button profileButton = (Button) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.editProfile();
            }
        });
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

    @Override
    public void showProfileForm() {
        Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}