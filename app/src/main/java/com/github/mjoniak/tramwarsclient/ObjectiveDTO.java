package com.github.mjoniak.tramwarsclient;

@SuppressWarnings("unused")
class ObjectiveDTO {
    private StopDTO endStop;
    private int points;

    StopDTO getEndStop() {
        return endStop;
    }

    int getPoints() { return points; }
}
