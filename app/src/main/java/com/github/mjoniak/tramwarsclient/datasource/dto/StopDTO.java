package com.github.mjoniak.tramwarsclient.datasource.dto;

@SuppressWarnings("unused")
public class StopDTO {
    private String name;
    private float lat;
    private float lon;

    StopDTO() {}

    public StopDTO(String name, float lat, float lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
