package com.github.mjoniak.tramwarsclient.datasource.dto;

@SuppressWarnings("unused")
public class AuthorisationTokenDTO {
    private String access_token;
    private String token_type;

    public String getAccessToken() {
        return access_token;
    }

    public String getTokenType() {
        return token_type;
    }
}
