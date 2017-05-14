package com.github.mjoniak.tramwarsclient.domain;

public class UserProfile {
    private String name;
    private String password;
    @SuppressWarnings("unused")
    private int score;

    @SuppressWarnings("unused")
    public UserProfile() {}

    public UserProfile(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }
}
