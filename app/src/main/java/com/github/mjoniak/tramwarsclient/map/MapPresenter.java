package com.github.mjoniak.tramwarsclient.map;

import android.support.annotation.NonNull;

import com.github.mjoniak.tramwarsclient.ApplicationState;
import com.github.mjoniak.tramwarsclient.datasource.ApiClient;
import com.github.mjoniak.tramwarsclient.datasource.IContinuation;
import com.github.mjoniak.tramwarsclient.datasource.dto.ObjectiveDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.StopDTO;
import com.github.mjoniak.tramwarsclient.domain.Geography;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

class MapPresenter implements MapContract.Presenter {
    private final ApiClient client;
    private MapContract.View view;

    MapPresenter(MapContract.View view, ApiClient client) {
        this.view = view;
        this.client = client;
    }

    @Override
    public void editProfile() {
        view.showProfileForm();
    }

    @Override
    public void handlePositionChange(LatLng pos, List<StopDTO> stops) {
        view.centerMapOn(pos);
        List<StopDTO> closeStops = findCloseStops(pos, stops);
        view.updateStopsCount(closeStops.size());

        ApplicationState state = ApplicationState.getInstance();
        state.setPosition(pos);
        if (state.hasObjective()) {
            client.postPosition(pos, state.getRouteId(), state.getAccessToken());
        }
    }

    @Override
    public void chooseInitialStop(List<StopDTO> stops) {
        if (ApplicationState.getInstance().hasObjective()) return;

        List<StopDTO> closeStops = findCloseStops(ApplicationState.getInstance().getPosition(), stops);

        if (closeStops.size() > 1) {
            view.displayCloseStops(closeStops);
        } else {
            chooseFinalStop(closeStops.get(0));
        }
    }

    public void chooseFinalStop(final StopDTO stop) {
        final String serialized = stop.serialize();
        ApplicationState appState = ApplicationState.getInstance();
        if (appState.objectiveFinished(appState.getPosition())) {
            view.objectiveFinished();
            appState.resetObjective();
        }

        if (appState.hasObjective()) {
            return;
        }

        client.getObjectives(appState.getAccessToken(), serialized, new IContinuation<ObjectiveDTO[]>() {
            @Override
            public void continueWith(ObjectiveDTO[] response) {
            view.displayObjectivesList(stop, response);
            }
        });
    }

    @NonNull
    private List<StopDTO> findCloseStops(LatLng pos, List<StopDTO> stops) {
        List<StopDTO> closeStops = new ArrayList<>();
        for(StopDTO stop : stops) {
            LatLng stopPos = new LatLng(stop.getLat(), stop.getLon());
            if (Geography.areClose(pos, stopPos)) {
                closeStops.add(stop);
            }
        }
        return closeStops;
    }
}
