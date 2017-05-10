package com.github.mjoniak.tramwarsclient.datasource.dto;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class AuthorisationDTO {

    private final String grantType;
    private final String username;
    private final String password;

    public AuthorisationDTO(String grantType, String username, String password) {
        this.grantType = grantType;
        this.username = username;
        this.password = password;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
