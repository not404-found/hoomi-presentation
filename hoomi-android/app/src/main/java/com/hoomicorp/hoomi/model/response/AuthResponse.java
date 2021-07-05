package com.hoomicorp.hoomi.model.response;

import com.google.gson.GsonBuilder;
import com.hoomicorp.hoomi.model.request.RegistrationRequest;

public class AuthResponse {
    private String token;
    private String userId;

    public AuthResponse() {
    }

    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, AuthResponse.class);
    }
}
