package com.github.mjoniak.tramwarsclient.datasource.dto;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class PositionDTO {
    private final double lat;
    private final double lng;

    public PositionDTO(double latitude, double longitude) {

        this.lat = latitude;
        this.lng = longitude;
    }
}
