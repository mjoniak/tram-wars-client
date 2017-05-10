package com.github.mjoniak.tramwarsclient.datasource.dto;

@SuppressWarnings("unused")
public class ObjectiveDTO {
    private StopDTO endStop;
    private int points;

    public StopDTO getEndStop() {
        return endStop;
    }

    public int getPoints() { return points; }
}
