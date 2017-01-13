package com.github.mjoniak.tramwarsclient;

import com.google.android.gms.maps.model.LatLng;

class ApplicationState {
    private static ApplicationState instance;

    private Objective currentObjective;

    private ApplicationState() {}

    static ApplicationState getInstance() {
        if (instance == null) {
            instance = new ApplicationState();
        }

        return instance;
    }

    void setObjective(Objective objective) {
        currentObjective = objective;
    }

    boolean hasObjective() {
        return currentObjective != null;
    }

    boolean objectiveFinished(LatLng currentPosition) {
        return hasObjective() && currentObjective.finished(currentPosition);
    }

    void resetObjective() {
        setObjective(null);
    }

    int getRouteId() {
        return currentObjective.getRouteId();
    }
}
