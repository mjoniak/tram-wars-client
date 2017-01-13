package com.github.mjoniak.tramwarsclient;

@SuppressWarnings("unused")
class StopDTO {
    private String name;
    private float lat;
    private float lon;

    StopDTO() {}

    StopDTO(String name, float lat, float lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    float getLat() {
        return lat;
    }

    float getLon() {
        return lon;
    }
}
