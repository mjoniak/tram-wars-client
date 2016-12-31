package com.github.mjoniak.tramwarsclient;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
class PositionDTO {
    private final double lat;
    private final double lng;

    PositionDTO(double latitude, double longitude) {

        this.lat = latitude;
        this.lng = longitude;
    }
}
