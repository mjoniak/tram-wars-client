package com.github.mjoniak.tramwarsclient;

class AuthorisationDTO {

    private final String grantType;
    private final String username;
    private final String password;

    AuthorisationDTO(String grantType, String username, String password) {
        this.grantType = grantType;
        this.username = username;
        this.password = password;
    }

    String getGrantType() {
        return grantType;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }
}
