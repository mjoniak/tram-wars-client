package com.github.mjoniak.tramwarsclient;

import com.github.mjoniak.tramwarsclient.domain.Objective;
import com.google.android.gms.maps.model.LatLng;

public class ApplicationState {
    private static ApplicationState instance;

    private Objective currentObjective;

    private ApplicationState() {}

    public static ApplicationState getInstance() {
        if (instance == null) {
            instance = new ApplicationState();
        }

        return instance;
    }

    public void setObjective(Objective objective) {
        currentObjective = objective;
    }

    public boolean hasObjective() {
        return currentObjective != null;
    }

    public boolean objectiveFinished(LatLng currentPosition) {
        return hasObjective() && currentObjective.finished(currentPosition);
    }

    public void resetObjective() {
        setObjective(null);
    }

    public int getRouteId() {
        return currentObjective.getRouteId();
    }
}
