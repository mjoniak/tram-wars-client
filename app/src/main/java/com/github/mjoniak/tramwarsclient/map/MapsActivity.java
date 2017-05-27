package com.github.mjoniak.tramwarsclient.map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjoniak.tramwarsclient.ApplicationState;
import com.github.mjoniak.tramwarsclient.Const;
import com.github.mjoniak.tramwarsclient.R;
import com.github.mjoniak.tramwarsclient.chooseobjective.ChooseObjectiveActivity;
import com.github.mjoniak.tramwarsclient.datasource.ApiClient;
import com.github.mjoniak.tramwarsclient.datasource.IContinuation;
import com.github.mjoniak.tramwarsclient.datasource.IErrorHandler;
import com.github.mjoniak.tramwarsclient.datasource.dto.ObjectiveDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.StopDTO;
import com.github.mjoniak.tramwarsclient.profile.ProfileActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity
        extends AppCompatActivity
        implements OnMapReadyCallback, OnRequestPermissionsResultCallback, MapContract.View {

    private static final int REQUEST_ACCESS_LOCATION = 3001;
    private static final long MIN_TIME = 1000L;
    private static final float MIN_DISTANCE = 3.0f;

    private final List<StopDTO> loadedStops = new ArrayList<>();

    private String accessToken;
    private MapContract.Presenter presenter;
    private GoogleMap googleMap;
    private ApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new ApiClient(this, new IErrorHandler() {
            @Override
            public void handle(String message) {
                Toast.makeText(MapsActivity.this, R.string.connectivity_error, Toast.LENGTH_LONG).show();
            }
        });

        presenter = new MapPresenter(this, client);

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
    public void onMapReady(GoogleMap googleMap) {
        final ApplicationState appState = ApplicationState.getInstance();

        if (!appState.hasPosition()) {
            appState.setPosition(new LatLng(50.0f, 20.0f));
        }

        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(appState.getPosition(), 15.0f));

        if (hasLocationAccessPermission()) {
            registerGpsUpdates();
        } else {
            requestPermissions();
        }

        client.getStops(accessToken, new IContinuation<StopDTO[]>() {
            @Override
            public void continueWith(StopDTO[] response) {
                displayStops(response);

                //TODO: possible race condition
                loadedStops.clear();
                loadedStops.addAll(Arrays.asList(response));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.found_stops_fab);
        setFabProperties(fab, R.color.black_overlay, false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.chooseInitialStop(loadedStops);
            }
        });
    }

    @Override
    public void updateStopsCount(int closeStops) {
        TextView foundStopsText = (TextView) findViewById(R.id.found_stops_text);
        foundStopsText.setText(closeStops + "");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.found_stops_fab);
        if (closeStops > 0) {
            setFabProperties(fab, R.color.contrast_overlay, true);
        } else {
            setFabProperties(fab, R.color.black_overlay, false);
        }
    }

    @Override
    public void displayCloseStops(List<StopDTO> closeStops) {
        final List<Marker> addedMarkers = new ArrayList<>();
        for (StopDTO stop : closeStops) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(stop.getLat(), stop.getLon()))
                    .title(stop.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            addedMarkers.add(marker);
        }

        googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                boolean contains = false;
                for (Marker m : addedMarkers) {
                    contains |= m.getPosition().latitude == marker.getPosition().latitude
                            && m.getPosition().longitude == marker.getPosition().longitude
                            && m.getTitle().equals(marker.getTitle());
                }

                if (!contains) return false;

                marker.getTitle();
                presenter.chooseFinalStop(new StopDTO(
                        marker.getTitle(),
                        (float) marker.getPosition().latitude,
                        (float) marker.getPosition().longitude));

                for (Marker m : addedMarkers) {
                    m.remove();
                }

                googleMap.setOnMarkerClickListener(null);
                return true;
            }
        });
    }

    @Override
    public void objectiveFinished() {
        Toast.makeText(MapsActivity.this, R.string.objective_finished, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayObjectivesList(StopDTO stop, ObjectiveDTO[] objectives) {
        Intent intent = new Intent(MapsActivity.this, ChooseObjectiveActivity.class);
        ArrayList<String> extraObjectives = new ArrayList<>();
        for (ObjectiveDTO objectiveDTO : objectives) {
            extraObjectives.add(objectiveDTO.getEndStop().serialize() + "," + objectiveDTO.getPoints());
        }

        intent.putExtra(Const.OBJECTIVES_EXTRA_KEY, extraObjectives);
        intent.putExtra(Const.POSITION_EXTRA_KEY, stop.serialize());
        startActivity(intent);
    }

    private void setFabProperties(FloatingActionButton fab, int color, boolean clickable) {
        ColorStateList csl = AppCompatResources.getColorStateList(this, color);
        fab.setBackgroundTintList(csl);
        fab.setClickable(clickable);
    }

    private void displayStops(StopDTO[] stops) {
        for (StopDTO stop : stops) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(stop.getLat(), stop.getLon()))
                    .title(stop.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    @Override
    public void centerMapOn(LatLng pos) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_LOCATION) {

            registerGpsUpdates();
        }
    }

    private void registerGpsUpdates() {
        if (!hasLocationAccessPermission()) {

            throw new SecurityException("GPS not allowed!");
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                presenter.handlePositionChange(new LatLng(location.getLatitude(), location.getLongitude()), loadedStops);
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

    private boolean hasLocationAccessPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void showProfileForm() {
        Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.INTERNET }, 0);
        ActivityCompat.requestPermissions(this, new String[] {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION },
                REQUEST_ACCESS_LOCATION);
    }
}