package com.github.mjoniak.tramwarsclient.map;

import android.content.Context;

import com.github.mjoniak.tramwarsclient.datasource.dto.ObjectiveDTO;
import com.github.mjoniak.tramwarsclient.datasource.dto.StopDTO;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

interface MapContract {
    interface View {
        void showProfileForm();

        void centerMapOn(LatLng pos);

        void updateStopsCount(int closeStops);

        void displayCloseStops(List<StopDTO> closeStops);

        void objectiveFinished();

        void displayObjectivesList(StopDTO stop, ObjectiveDTO[] objectives);

        void requestPermissions();
    }

    interface Presenter {
        void editProfile();

        void handlePositionChange(LatLng pos, List<StopDTO> stops);

        void chooseInitialStop(List<StopDTO> stops);

        void chooseFinalStop(final StopDTO stop);
    }
}
