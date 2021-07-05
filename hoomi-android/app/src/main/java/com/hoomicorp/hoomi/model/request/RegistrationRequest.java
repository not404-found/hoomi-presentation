package com.hoomicorp.hoomi.model.request;

import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class RegistrationRequest implements Request {
    private String email;
    private String password;
    private String phoneNum;
    private String username;
    private String dateOfBirth;

    public RegistrationRequest(String email, String password, String phoneNum, String username, String dateOfBirth) {
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.username = username;
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getUsername() {
        return username;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, RegistrationRequest.class);
    }
}
